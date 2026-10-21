package um.tesoreria.core.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import um.tesoreria.core.event.PaymentProcessedEvent;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.service.MercadoPagoContextService;

import jakarta.annotation.PostConstruct;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventListener {

    private final MercadoPagoContextService mercadoPagoContextService;

    @PostConstruct
    public void init() {
        log.info("\n\nPaymentEventListener initialized\n\n");
    }

    /**
     * Sin {@code try/catch}: si el procesamiento falla, la excepción tiene que llegar al
     * {@code DefaultErrorHandler} del contenedor para que Kafka reintente con backoff y, agotados
     * los intentos, derive el record a {@code payment-processed.DLT}. Tragarse el error acá
     * commiteaba el offset y perdía el pago.
     */
    @KafkaListener(topics = "payment-processed", groupId = "tesoreria-core-group", containerFactory = "kafkaListenerContainerFactory")
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.debug("\n\nReceived payment event: {}\n\n", event);
        mercadoPagoContextService.processPaymentEvent(event);
    }
}
