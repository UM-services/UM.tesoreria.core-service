package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model;

/** Estados posibles de un job de regeneración masiva. */
public enum AsientoIngresoJobEstado {

    PENDIENTE,
    EN_PROCESO,
    COMPLETADO,
    CANCELADO,
    ERROR

}
