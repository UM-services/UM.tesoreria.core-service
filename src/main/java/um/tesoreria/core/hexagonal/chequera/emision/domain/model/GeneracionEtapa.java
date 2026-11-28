package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

/**
 * Etapa del job de generación masiva de borradores.
 *
 * <p>{@code BORRADORES}: crea o reutiliza un borrador por cada serie del padrón (no escribe en
 * {@code chequera_*}). {@code EMISION}: emite esos borradores y actualiza la serie.</p>
 */
public enum GeneracionEtapa {
    BORRADORES,
    EMISION
}
