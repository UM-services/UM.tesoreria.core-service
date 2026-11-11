package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto;

public record ResultadoEliminacionResponse(
        String filename,
        int pagosEliminados,
        int pagosReemplazoEliminados,
        int archivosBancoEliminados,
        int cuotasRecalculadas) {
}
