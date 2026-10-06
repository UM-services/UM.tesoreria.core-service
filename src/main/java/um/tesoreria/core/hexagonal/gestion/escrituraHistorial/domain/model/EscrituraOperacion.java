package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.model;

/**
 * Tipo de escritura de negocio registrada en el historial transaccional.
 * No incluye identidad de usuario: el servidor no atribuye un actor verificado.
 */
public enum EscrituraOperacion {
    ALTA,
    EDICION,
    BAJA
}
