package um.tesoreria.core.hexagonal.chequera.envio.domain.model;

/**
 * Respuesta al reclamo atómico de una solicitud por parte del sender.
 *
 * <p>{@code puedeEnviar} es {@code false} si la chequera ya se envió, si otro proceso tiene el
 * lock vigente o si se agotaron los intentos. En esos casos el sender debe hacer ack sin enviar
 * (o derivar a revisión según el motivo).</p>
 */
public record EnvioSolicitudClaim(EnvioSolicitud solicitud, boolean puedeEnviar, String motivo) {
}
