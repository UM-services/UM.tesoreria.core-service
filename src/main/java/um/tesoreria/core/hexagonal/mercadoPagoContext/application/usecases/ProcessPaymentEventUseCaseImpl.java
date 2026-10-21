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

@Component
@Slf4j
public class ProcessPaymentEventUseCaseImpl implements ProcessPaymentEventUseCase {

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

    /**
     * Aplica el evento al contexto y dispara el único efecto no idempotente por sí mismo: el
     * registro del pago de cuota.
     * <p>
     * No se corta el evento completo cuando el {@code paymentId} ya fue procesado: un refund o un
     * chargeback del mismo pago es una transición legítima del estado y tiene que persistirse. La
     * idempotencia vive en los efectos: {@code PagoService.registraPagoMP} no crea un segundo pago
     * para el mismo {@code idMercadoPago} y marcar la reserva como pagada es un no-op si ya lo está.
     * <p>
     * Tampoco se capturan excepciones: si un efecto falla, la transacción revierte y el
     * {@code DefaultErrorHandler} de Kafka reintenta o deriva a {@code payment-processed.DLT}.
     * <p>
     * Orden: como la key del tópico es el {@code paymentId}, dos pagos del mismo contexto pueden
     * llegar desordenados. Un evento con {@code dateLastUpdated} anterior al ya aplicado se
     * descarta (ver {@link #eventoDesactualizado}).
     */
    @Override
    public void processPaymentEvent(PaymentProcessedEvent event) {
        log.debug("\n\nProcessing payment event for contextId: {}\n\n", event.getMercadoPagoContextId());
        var context = findByMercadoPagoContextIdUseCase.findByMercadoPagoContextId(event.getMercadoPagoContextId());

        if (eventoDesactualizado(context, event)) {
            log.warn("Evento descartado para contexto {} (paymentId={}, source={}): dateLastUpdated {} es anterior al ya aplicado {}",
                    event.getMercadoPagoContextId(), event.getPaymentId(), event.getSource(),
                    event.getDateLastUpdated(), context.getDateLastUpdated());
            return;
        }

        if (context.getReservaVacanteId() != null) {
            // Es un pago de reserva de vacante
            if (!Objects.equals(context.getReservaVacanteId(), event.getReservaVacanteId())) {
                log.error("Mismatch reservaVacanteId for contextId: {} (context: {}, event: {})", 
                        event.getMercadoPagoContextId(), context.getReservaVacanteId(), event.getReservaVacanteId());
                return;
            }

            context = aplicarDatosDelPago(context, event);

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

            context = aplicarDatosDelPago(context, event);

            if ("approved".equals(context.getStatus())) {
                pagoService.registraPagoMP(context.getMercadoPagoContextId());
            }
        }
    }

    private MercadoPagoContext aplicarDatosDelPago(MercadoPagoContext context, PaymentProcessedEvent event) {
        context.setIdMercadoPago(event.getPaymentId());
        context.setPayment(event.getPaymentJson());
        context.setImportePagado(event.getTransactionAmount());
        context.setFechaPago(event.getDateApproved());
        context.setFechaAcreditacion(event.getDateApproved());
        context.setStatus(event.getStatus());
        // Sólo se pisa con un valor presente: un productor viejo (sin el campo) no debe borrar la marca.
        if (event.getDateLastUpdated() != null) {
            context.setDateLastUpdated(event.getDateLastUpdated());
        }

        return updateMercadoPagoContextUseCase.update(context, context.getMercadoPagoContextId());
    }

    /**
     * Un evento es viejo si trae {@code dateLastUpdated} y es anterior al último aplicado. Los
     * eventos sin el campo (productor anterior a la fase 3) se aplican como siempre.
     */
    private boolean eventoDesactualizado(MercadoPagoContext context, PaymentProcessedEvent event) {
        return event.getDateLastUpdated() != null
                && context.getDateLastUpdated() != null
                && event.getDateLastUpdated().isBefore(context.getDateLastUpdated());
    }
}
