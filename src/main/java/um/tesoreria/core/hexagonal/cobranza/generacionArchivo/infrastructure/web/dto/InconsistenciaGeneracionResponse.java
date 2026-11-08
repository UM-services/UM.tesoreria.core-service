package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto;

public record InconsistenciaGeneracionResponse(
        Long chequeraCuotaId,
        Long debitoId,
        String descripcion) {
}
