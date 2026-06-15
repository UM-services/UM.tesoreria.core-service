package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import um.tesoreria.core.event.PaymentProcessedEvent;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.FindByMercadoPagoContextIdUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.ProcessPaymentEventUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.UpdateMercadoPagoContextUseCase;
import um.tesoreria.core.service.facade.PagoService;

import java.util.Objects;

@Component
@Slf4j
public class ProcessPaymentEventUseCaseImpl implements ProcessPaymentEventUseCase {

    private final FindByMercadoPagoContextIdUseCase findByMercadoPagoContextIdUseCase;
    private final UpdateMercadoPagoContextUseCase updateMercadoPagoContextUseCase;
    private final PagoService pagoService;

    public ProcessPaymentEventUseCaseImpl(
            FindByMercadoPagoContextIdUseCase findByMercadoPagoContextIdUseCase,
            UpdateMercadoPagoContextUseCase updateMercadoPagoContextUseCase,
            @Lazy PagoService pagoService) {
        this.findByMercadoPagoContextIdUseCase = findByMercadoPagoContextIdUseCase;
        this.updateMercadoPagoContextUseCase = updateMercadoPagoContextUseCase;
        this.pagoService = pagoService;
    }

    @Override
    public void processPaymentEvent(PaymentProcessedEvent event) {
        log.debug("\n\nProcessing payment event for contextId: {}\n\n", event.getMercadoPagoContextId());
        var context = findByMercadoPagoContextIdUseCase.findByMercadoPagoContextId(event.getMercadoPagoContextId());

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
