package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

/** Acción que la reconstrucción de traza aplica sobre un asiento propuesto. */
public enum ReconstruccionAccion {
    CREAR,
    ACTUALIZAR,
    SIN_CAMBIOS
}
