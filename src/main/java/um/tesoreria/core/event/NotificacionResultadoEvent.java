package um.tesoreria.core.event;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Resultado del envío publicado por el sender en {@code notificacion-resultado}. Se usa para
 * marcar el estado de negocio sólo cuando el correo salió efectivamente.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificacionResultadoEvent {

    private String eventId;
    private OffsetDateTime occurredAt;
    private boolean enviado;
    private String error;
    private ReferenciaNotificacion referencia;

}
