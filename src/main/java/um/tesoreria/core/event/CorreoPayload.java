package um.tesoreria.core.event;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contenido de un correo genérico (sin adjuntos). Los flujos con documento usan eventos con IDs
 * y el sender genera el PDF.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorreoPayload {

    @Builder.Default
    private Remitente remitente = Remitente.NOTIFICACION;

    @Builder.Default
    private List<String> para = new ArrayList<>();

    private String replyTo;
    private String asunto;
    private String cuerpo;

    @Builder.Default
    private boolean html = false;

    private ReferenciaNotificacion referencia;

}
