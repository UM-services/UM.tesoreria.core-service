package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in;

import um.tesoreria.core.event.PaymentProcessedEvent;

public interface ProcessPaymentEventUseCase {
    void processPaymentEvent(PaymentProcessedEvent event);
}
