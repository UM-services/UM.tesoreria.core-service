package um.tesoreria.core.service;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import um.tesoreria.core.event.CorreoEvent;
import um.tesoreria.core.event.CorreoPayload;
import um.tesoreria.core.event.SendChequeraEvent;
import um.tesoreria.core.event.SendCuotaEvent;

/**
 * Publica en Kafka las solicitudes de correo. Core no habla SMTP: el único servicio que envía es
 * {@code tesoreria-sender}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CorreoEventPublisher {

    public static final String TOPIC_CORREO = "send-correo";
    public static final String TOPIC_CHEQUERA = "send-chequera";
    public static final String TOPIC_CUOTA = "send-cuota";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public String publicar(CorreoPayload payload) {
        var event = CorreoEvent.builder()
                .eventType(CorreoEvent.TIPO)
                .version(CorreoEvent.VERSION_ACTUAL)
                .eventId(UUID.randomUUID().toString())
                .occurredAt(OffsetDateTime.now())
                .payload(payload)
                .build();
        kafkaTemplate.send(TOPIC_CORREO, event.getEventId(), event);
        log.debug("CorreoEvent publicado en {}: {}", TOPIC_CORREO, event.getEventId());
        return event.getEventId();
    }

    public void publicarChequera(SendChequeraEvent event) {
        kafkaTemplate.send(TOPIC_CHEQUERA, event);
        log.debug("SendChequeraEvent publicado en {}: {}", TOPIC_CHEQUERA, event);
    }

    public void publicarCuota(Long chequeraCuotaId) {
        var event = SendCuotaEvent.builder().chequeraCuotaId(chequeraCuotaId).build();
        kafkaTemplate.send(TOPIC_CUOTA, event);
        log.debug("SendCuotaEvent publicado en {}: {}", TOPIC_CUOTA, event);
    }

}
