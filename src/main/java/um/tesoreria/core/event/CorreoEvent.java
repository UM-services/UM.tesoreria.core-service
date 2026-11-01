package um.tesoreria.core.event;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Envelope del topic {@code send-correo}. {@code eventType} es un string estable (nunca un
 * FQCN) y {@code version} sólo se incrementa ante cambios incompatibles del payload.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorreoEvent {

    public static final String TIPO = "CORREO";
    public static final int VERSION_ACTUAL = 1;

    private String eventType;
    private Integer version;
    private String eventId;
    private OffsetDateTime occurredAt;
    private CorreoPayload payload;

}
