package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto;

public record ResultadoOperacionDebitoResponse(
        String operacion,
        int registros,
        int omitidos) {
}
