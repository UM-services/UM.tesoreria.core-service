package um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model;

/**
 * Estado de una cuota para mostrar. Se evalúa en este orden y gana la primera regla que se cumple:
 * baja, compensada, pagada, sin importe definido, vencida y, si no, pendiente.
 */
public enum EstadoCuota {
    BAJA,
    COMPENSADA,
    PAGADA,
    A_DEFINIR,
    VENCIDA,
    PENDIENTE
}
