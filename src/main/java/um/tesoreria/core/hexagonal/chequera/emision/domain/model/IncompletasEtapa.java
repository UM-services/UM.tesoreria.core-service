package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

/**
 * Etapa del job de chequeras incompletas.
 *
 * <p>{@code BORRADORES}: crea/reutiliza un borrador por serie completando las cuotas faltantes.
 * {@code EMISION}: emite esos borradores (escribe en {@code chequera_*}) y extiende el débito.</p>
 */
public enum IncompletasEtapa {
    BORRADORES,
    EMISION
}
