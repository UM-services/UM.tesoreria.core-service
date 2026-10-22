package um.tesoreria.core.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import um.tesoreria.core.event.NotificacionResultadoEvent;
import um.tesoreria.core.service.PersonaSuspendidoService;

/**
 * Consume el resultado del envío publicado por {@code tesoreria-sender}. Sólo cuando el correo
 * salió efectivamente se marca {@code persona_suspendido.notificado = 1}; antes se marcaba de
 * forma optimista y un fallo de SMTP dejaba al alumno como notificado sin haber recibido nada.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class NotificacionResultadoListener {

    private static final String TIPO_PERSONA_SUSPENDIDO = "PERSONA_SUSPENDIDO";

    private final PersonaSuspendidoService personaSuspendidoService;

    @KafkaListener(topics = "notificacion-resultado", groupId = "tesoreria-core-group",
            containerFactory = "notificacionResultadoKafkaListenerContainerFactory")
    public void handleResultado(NotificacionResultadoEvent event) {
        log.debug("Received notificacion-resultado: {}", event);
        if (event == null) {
            return;
        }
        if (!event.isEnviado()) {
            log.warn("Notificación {} NO enviada: {}", event.getEventId(), event.getError());
            return;
        }
        var referencia = event.getReferencia();
        if (referencia == null || !TIPO_PERSONA_SUSPENDIDO.equals(referencia.getTipo())) {
            return;
        }
        if (referencia.getId() == null || referencia.getId().isBlank()) {
            log.warn("Resultado {} sin id de personaSuspendido: no se puede marcar", event.getEventId());
            return;
        }
        try {
            Long personaSuspendidoId = Long.valueOf(referencia.getId());
            var personaSuspendido = personaSuspendidoService.findByPersonaSuspendidoId(personaSuspendidoId);
            personaSuspendido.setNotificado((byte) 1);
            personaSuspendidoService.update(personaSuspendido, personaSuspendidoId);
            log.info("Notificación {} confirmada: personaSuspendido {} marcada como notificada",
                    event.getEventId(), personaSuspendidoId);
        } catch (NumberFormatException e) {
            log.warn("Resultado {} con id no numérico: {}", event.getEventId(), referencia.getId());
        }
    }

}
