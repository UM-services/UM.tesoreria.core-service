package um.tesoreria.core.hexagonal.chequera.envio.domain.model;

/**
 * Estado de una fila del job.
 *
 * <p>{@link #ENCOLADA} es transitorio: la tarea se publicó a Kafka y espera el acuse del sender
 * ({@link #ENVIADA} o {@link #ERROR_SMTP}). {@link #RETENIDA} es histórico: la retención por deuda
 * se retiró y sólo queda para leer jobs viejos. {@link #SIN_CORREOS} marca las series omitidas
 * antes de publicar (D6).</p>
 */
public enum EnvioTareaEstado {
    PENDIENTE,
    ENCOLADA,
    ENVIADA,
    ERROR_SMTP,
    RETENIDA,
    SIN_CORREOS,
    CORREO_CAPTURADO,
    DEBITO_EXTENDIDO,
    OMITIDA,
    ERROR
}
