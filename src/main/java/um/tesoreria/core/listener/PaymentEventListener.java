package um.tesoreria.core.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import um.tesoreria.core.event.PaymentProcessedEvent;
import um.tesoreria.core.service.MercadoPagoContextService;

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

    @KafkaListener(topics = "payment-processed", groupId = "tesoreria-core-group", containerFactory = "kafkaListenerContainerFactory")
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.debug("\n\nReceived payment event: {}\n\n", event);
        try {
            mercadoPagoContextService.processPaymentEvent(event);
        } catch (Exception e) {
            log.error("Error processing payment event: {}", e.getMessage(), e);
        }
    }
}
