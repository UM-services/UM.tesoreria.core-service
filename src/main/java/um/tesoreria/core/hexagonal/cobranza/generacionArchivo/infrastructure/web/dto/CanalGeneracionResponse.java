package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto;

public record CanalGeneracionResponse(
        String codigo,
        String slug,
        String nombre) {
}
