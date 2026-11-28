package um.tesoreria.core.hexagonal.chequera.envio.domain.model;

/**
 * Estado de una solicitud de envío de chequera.
 *
 * <p>La solicitud es la unidad idempotente del envío: existe una sola por chequera (clave de
 * {@code chequera_serie}) y sobrevive a reentregas de Kafka, reintentos y reinicios. Los estados
 * terminales sin envío ({@link #SIN_CUOTAS}, {@link #SIN_CORREOS}, {@link #RETENIDA},
 * {@link #OMITIDA}) no vuelven a mandar correo; {@link #FALLIDO} sí puede reintentarse hasta el
 * máximo configurado. {@link #RETENIDA} es histórico: la retención por deuda se retiró.</p>
 */
public enum EnvioSolicitudEstado {
    PENDIENTE,
    ENVIANDO,
    ENVIADO,
    FALLIDO,
    OMITIDA,
    SIN_CORREOS,
    SIN_CUOTAS,
    RETENIDA
}
