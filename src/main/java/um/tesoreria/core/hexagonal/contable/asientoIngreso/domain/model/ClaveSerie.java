package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model;

/** Clave de una chequera serie: {@code (facultad, tipo de chequera, serie)}. */
public record ClaveSerie(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
}
