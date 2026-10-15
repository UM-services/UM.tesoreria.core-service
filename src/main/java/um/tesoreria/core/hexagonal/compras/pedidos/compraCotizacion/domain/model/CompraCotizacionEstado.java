package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model;

/**
 * Estados del proceso de cotización.
 *
 * <p>El dpto. de compras arma el {@code BORRADOR} (ítems combinados + proveedores), lo envía
 * ({@code ENVIADA}: reserva el número {@code SC-AAAA-NNNNNN} y habilita a los proveedores). El
 * cierre y la recomendación son de la etapa de respuestas (6C).</p>
 */
public enum CompraCotizacionEstado {

    BORRADOR,
    ENVIADA,
    RECOMENDADA,
    ADJUDICADA,
    DEVUELTA_A_COMPRAS,
    NO_ADJUDICADA,
    /** Adjudicado y con la Orden de Compra emitida (etapa 8). */
    FORMALIZADA,
    CERRADA,
    ANULADA;

    /** Sólo el borrador se edita (ítems y proveedores). */
    public boolean esEditable() {
        return this == BORRADOR;
    }

    /** Se puede anular mientras no esté cerrada. */
    public boolean esAnulable() {
        return this == BORRADOR || this == ENVIADA;
    }
}
