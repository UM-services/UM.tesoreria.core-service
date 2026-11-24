package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

/**
 * Referencia mínima de un borrador para agregados por facultad y sede: sólo la facultad y el
 * tipo de chequera, del que se deriva la sede (igual que {@code chequera_serie.geografica}).
 */
public record BorradorSede(Integer facultadId, Integer tipoChequeraId) {
}
