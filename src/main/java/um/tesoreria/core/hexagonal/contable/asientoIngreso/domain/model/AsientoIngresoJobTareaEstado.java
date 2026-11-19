package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model;

/** Estados posibles de una tarea (fecha + tipo de pago) dentro de un job. */
public enum AsientoIngresoJobTareaEstado {

    PENDIENTE,
    OK,
    SIN_MOVIMIENTOS,
    ERROR

}
