package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto;

public record CuentaFaltanteResponse(
        Integer facultadId,
        String facultadNombre,
        Integer lectivoId,
        String lectivoNombre,
        Integer tipoChequeraId,
        String tipoChequeraNombre,
        Integer productoId,
        String productoNombre,
        Integer geograficaId,
        String geograficaNombre) {
}
