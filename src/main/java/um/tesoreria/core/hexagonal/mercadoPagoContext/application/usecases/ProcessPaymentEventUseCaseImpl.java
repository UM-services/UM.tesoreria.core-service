package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import um.tesoreria.core.event.PaymentProcessedEvent;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.FindByMercadoPagoContextIdUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.ProcessPaymentEventUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.UpdateMercadoPagoContextUseCase;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in.FindReservaVacanteUseCase;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in.UpdateReservaVacanteUseCase;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.service.facade.PagoService;

import java.util.Objects;
import java.util.Set;

/**
 * Procesa los eventos de pago que llegan por Kafka desde
 * tesoreria-mercadopago-service (tópico "payment-processed") y decide qué
 * hacer según el resultado del pago.
 * <p>
 * Un mismo MercadoPagoContext sirve para dos negocios distintos, que se
 * distinguen por qué id trae seteado:
 * <ul>
 *   <li>{@code reservaVacanteId != null} → pago de una reserva de vacante</li>
 *   <li>{@code chequeraCuotaId} (el otro caso) → pago de una cuota de chequera</li>
 * </ul>
 * Cada rama valida primero que el id del evento coincida con el del contexto
 * guardado (si no coincide, hay un desincronismo raro entre lo que se
 * publicó y lo que se esperaba, y se corta ahí sin tocar nada — ver
 * log.error "Mismatch..."). Si coincide, se vuelca el resultado del pago al
 * contexto (status, importe, fechas, etc.) y se persiste.
 * <p>
 * Recién ahí se decide la acción según el status:
 * <ul>
 *   <li><b>Cuota</b>: "approved" → {@link PagoService#registraPagoMP}
 *       (da de alta el pago). Alguno de los estados de
 *       {@link #ESTADOS_REVERSION} → {@link PagoService#revertirPagoMP}
 *       (deshace un pago ya registrado). Cualquier otro estado no dispara
 *       nada más allá de la actualización del contexto.</li>
 *   <li><b>Reserva de vacante</b>: "approved" marca la reserva como
 *       "pagado". No tiene reversión — está fuera del alcance actual;
 *       ningún estado negativo hace nada del lado de la reserva todavía.</li>
 * </ul>
 */
@Component
@Slf4j
public class ProcessPaymentEventUseCaseImpl implements ProcessPaymentEventUseCase {

    // Estados de MercadoPago que implican deshacer un pago de cuota ya
    // registrado. Alcance actual: "rejected" (nunca se cobró) + "refunded" y
    // "charged_back" (se cobró y la plata volvió al alumno, ya sea por
    // reembolso o por contracargo bancario). "cancelled" e "in_mediation"
    // siguen afuera por ahora. Cuando se habiliten, solo hay que sumar el
    // estado a este Set.
    //
    // OJO: "refunded" y "charged_back" son justo los dos estados con más
    // riesgo de toparse con un pago que YA tiene factura electrónica o
    // asiento contable generado (son eventos tardíos: llegan después de que
    // el pago ya estuvo "approved", a veces días o semanas después).
    // revertirPagoMP hoy borra el ChequeraPago sin chequear ninguna de las
    // dos cosas — ver issue. Confirmar con contaduría antes de que esto
    // llegue a un ambiente con datos reales.
    private static final Set<String> ESTADOS_REVERSION = Set.of("rejected", "refunded", "charged_back");

    private final FindByMercadoPagoContextIdUseCase findByMercadoPagoContextIdUseCase;
    private final UpdateMercadoPagoContextUseCase updateMercadoPagoContextUseCase;
    private final PagoService pagoService;
    private final FindReservaVacanteUseCase findReservaVacanteUseCase;
    private final UpdateReservaVacanteUseCase updateReservaVacanteUseCase;

    public ProcessPaymentEventUseCaseImpl(
            FindByMercadoPagoContextIdUseCase findByMercadoPagoContextIdUseCase,
            UpdateMercadoPagoContextUseCase updateMercadoPagoContextUseCase,
            @Lazy PagoService pagoService,
            FindReservaVacanteUseCase findReservaVacanteUseCase,
            UpdateReservaVacanteUseCase updateReservaVacanteUseCase) {
        this.findByMercadoPagoContextIdUseCase = findByMercadoPagoContextIdUseCase;
        this.updateMercadoPagoContextUseCase = updateMercadoPagoContextUseCase;
        this.pagoService = pagoService;
        this.findReservaVacanteUseCase = findReservaVacanteUseCase;
        this.updateReservaVacanteUseCase = updateReservaVacanteUseCase;
    }

    @Override
    public void processPaymentEvent(PaymentProcessedEvent event) {
        log.debug("\n\nProcessing payment event for contextId: {}\n\n", event.getMercadoPagoContextId());
        var context = findByMercadoPagoContextIdUseCase.findByMercadoPagoContextId(event.getMercadoPagoContextId());

        if (context.getReservaVacanteId() != null) {
            // Es un pago de reserva de vacante
            if (!Objects.equals(context.getReservaVacanteId(), event.getReservaVacanteId())) {
                log.error("Mismatch reservaVacanteId for contextId: {} (context: {}, event: {})",
                        event.getMercadoPagoContextId(), context.getReservaVacanteId(), event.getReservaVacanteId());
                return;
            }

            context.setIdMercadoPago(event.getPaymentId());
            context.setPayment(event.getPaymentJson());
            context.setImportePagado(event.getTransactionAmount());
            context.setFechaPago(event.getDateApproved());
            context.setFechaAcreditacion(event.getDateApproved());
            context.setStatus(event.getStatus());

            context = updateMercadoPagoContextUseCase.update(context, context.getMercadoPagoContextId());

            if ("approved".equals(context.getStatus())) {
                ReservaVacante reservaVacante = findReservaVacanteUseCase.findByReservaVacanteId(context.getReservaVacanteId());
                reservaVacante.setEstado("pagado");
                updateReservaVacanteUseCase.update(reservaVacante, context.getReservaVacanteId());
                log.info("ReservaVacante {} marked as paid.", context.getReservaVacanteId());
            }
        } else {
            // Es un pago de cuota
            if (!Objects.equals(context.getChequeraCuotaId(), event.getChequeraCuotaId())) {
                log.error("Mismatch chequeraCuotaId for contextId: {}", event.getMercadoPagoContextId());
                return;
            }

            context.setIdMercadoPago(event.getPaymentId());
            context.setPayment(event.getPaymentJson());
            context.setImportePagado(event.getTransactionAmount());
            context.setFechaPago(event.getDateApproved());
            context.setFechaAcreditacion(event.getDateApproved());
            context.setStatus(event.getStatus());

            context = updateMercadoPagoContextUseCase.update(context, context.getMercadoPagoContextId());

            if ("approved".equals(context.getStatus())) {
                pagoService.registraPagoMP(context.getMercadoPagoContextId());
            } else if (ESTADOS_REVERSION.contains(context.getStatus())) {
                pagoService.revertirPagoMP(context.getMercadoPagoContextId());
            }
        }
    }
}