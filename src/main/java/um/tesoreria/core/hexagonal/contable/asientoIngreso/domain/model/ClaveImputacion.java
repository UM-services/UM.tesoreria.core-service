package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model;

/** Clave de una imputación contable: {@code (facultad, lectivo, tipo de chequera, producto)}. */
public record ClaveImputacion(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId) {
}
