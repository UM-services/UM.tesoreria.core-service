package um.tesoreria.core.hexagonal.chequera.envio.domain.model;

/**
 * Resultado de crear (o recuperar) una solicitud.
 *
 * <p>{@code publicar} indica si el productor debe emitir el evento en Kafka. Es {@code false}
 * cuando la chequera ya fue enviada o hay un envío en curso: en ese caso el evento es un no-op
 * idempotente.</p>
 */
public record EnvioSolicitudCreacion(EnvioSolicitud solicitud, boolean publicar) {
}
