package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model;

/**
 * Decisión de la autoridad competente sobre la cotización recomendada (etapa 7).
 *
 * <ul>
 *   <li>{@code ADJUDICADA}: se adjudica la oferta (la recomendada u otra).</li>
 *   <li>{@code DEVUELTA_A_COMPRAS}: se devuelve al dpto. de compras para revisar la recomendación.</li>
 *   <li>{@code NO_ADJUDICADA}: no se adjudica (se descarta el proceso).</li>
 * </ul>
 */
public enum CompraAdjudicacionDecision {

    ADJUDICADA,
    DEVUELTA_A_COMPRAS,
    NO_ADJUDICADA;

    /** Estado del proceso de cotización resultante de la decisión. */
    public CompraCotizacionEstado estadoProceso() {
        return CompraCotizacionEstado.valueOf(name());
    }
}
