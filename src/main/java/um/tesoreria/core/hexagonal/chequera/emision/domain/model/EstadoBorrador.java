package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

/**
 * Ciclo de vida de un borrador de emisión de chequera.
 */
public enum EstadoBorrador {
    ABIERTO,
    EMITIDO,
    DESCARTADO
}
