package um.tesoreria.core.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import um.tesoreria.core.event.NotificacionResultadoEvent;
import um.tesoreria.core.hexagonal.chequera.envio.application.service.EnvioJobService;
import um.tesoreria.core.service.PersonaSuspendidoService;

/**
 * Consume el resultado del envío publicado por {@code tesoreria-sender}.
 *
 * <p>Dos correlaciones:</p>
 * <ul>
 *   <li>{@code PERSONA_SUSPENDIDO}: sólo cuando el correo salió se marca
 *       {@code persona_suspendido.notificado = 1} (antes se marcaba de forma optimista y un fallo
 *       de SMTP dejaba al alumno como notificado sin haber recibido nada).</li>
 *   <li>{@code CHEQUERA}: cierra la tarea del envío masivo ({@code frmCheqMail}) en
 *       {@code ENVIADA} o {@code ERROR_SMTP}.</li>
 * </ul>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class NotificacionResultadoListener {

    private static final String TIPO_PERSONA_SUSPENDIDO = "PERSONA_SUSPENDIDO";
    private static final String TIPO_CHEQUERA = "CHEQUERA";

    private final PersonaSuspendidoService personaSuspendidoService;
    private final EnvioJobService envioJobService;

    @KafkaListener(topics = "notificacion-resultado", groupId = "tesoreria-core-group",
            containerFactory = "notificacionResultadoKafkaListenerContainerFactory")
    public void handleResultado(NotificacionResultadoEvent event) {
        log.debug("Received notificacion-resultado: {}", event);
        if (event == null) {
            return;
        }
        var referencia = event.getReferencia();
        if (referencia == null) {
            log.warn("Resultado {} sin referencia: no se puede correlacionar", event.getEventId());
            return;
        }
        if (TIPO_PERSONA_SUSPENDIDO.equals(referencia.getTipo())) {
            handlePersonaSuspendido(event, referencia.getId());
            return;
        }
        if (TIPO_CHEQUERA.equals(referencia.getTipo())) {
            handleChequera(event, referencia.getId());
            return;
        }
        log.debug("Resultado {} con referencia no soportada: {}", event.getEventId(), referencia.getTipo());
    }

    private void handlePersonaSuspendido(NotificacionResultadoEvent event, String id) {
        if (!event.isEnviado()) {
            log.warn("Notificación {} NO enviada: {}", event.getEventId(), event.getError());
            return;
        }
        if (id == null || id.isBlank()) {
            log.warn("Resultado {} sin id de personaSuspendido: no se puede marcar", event.getEventId());
            return;
        }
        try {
            Long personaSuspendidoId = Long.valueOf(id);
            var personaSuspendido = personaSuspendidoService.findByPersonaSuspendidoId(personaSuspendidoId);
            personaSuspendido.setNotificado((byte) 1);
            personaSuspendidoService.update(personaSuspendido, personaSuspendidoId);
            log.info("Notificación {} confirmada: personaSuspendido {} marcada como notificada",
                    event.getEventId(), personaSuspendidoId);
        } catch (NumberFormatException e) {
            log.warn("Resultado {} con id no numérico: {}", event.getEventId(), id);
        }
    }

    private void handleChequera(NotificacionResultadoEvent event, String id) {
        if (id == null || id.isBlank()) {
            log.warn("Resultado {} sin chequeraId: no se puede cerrar la tarea", event.getEventId());
            return;
        }
        try {
            Long chequeraId = Long.valueOf(id);
            boolean aplicado = envioJobService.registrarAcuse(chequeraId, event.isEnviado(), event.getError());
            if (aplicado) {
                log.info("Envío masivo: chequera {} confirmada como {} por el sender", chequeraId,
                        event.isEnviado() ? "ENVIADA" : "ERROR_SMTP");
            }
        } catch (NumberFormatException e) {
            log.warn("Resultado {} con chequeraId no numérico: {}", event.getEventId(), id);
        } catch (RuntimeException e) {
            log.error("Resultado {} no se pudo aplicar a la chequera {}: {}", event.getEventId(), id,
                    e.getMessage());
        }
    }
}
