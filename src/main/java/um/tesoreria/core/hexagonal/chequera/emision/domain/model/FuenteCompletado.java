package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

/**
 * Fuente usada para completar las cuotas faltantes de un borrador.
 *
 * <ul>
 *   <li>{@code DISENO}: cuotas del diseño (lectivo_cuota) del tipo de chequera.</li>
 *   <li>{@code PLANTILLA}: cuotas de la plantilla de arancel (lectivo_cuota con el
 *       porcentaje de arancel del tipo seleccionado).</li>
 *   <li>{@code GUARDADAS}: cuotas ya persistidas en chequera_cuota para la serie.</li>
 * </ul>
 */
public enum FuenteCompletado {
    DISENO,
    PLANTILLA,
    GUARDADAS
}
