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
            }
        }
    }
}
