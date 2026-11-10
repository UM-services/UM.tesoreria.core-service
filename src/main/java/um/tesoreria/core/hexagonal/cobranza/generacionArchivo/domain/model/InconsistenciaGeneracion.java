package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model;

/**
 * Inconsistencia detectada durante la previsualización (no bloquea la generación).
 */
public record InconsistenciaGeneracion(
        Long chequeraCuotaId,
        Long debitoId,
        String descripcion) {
}
