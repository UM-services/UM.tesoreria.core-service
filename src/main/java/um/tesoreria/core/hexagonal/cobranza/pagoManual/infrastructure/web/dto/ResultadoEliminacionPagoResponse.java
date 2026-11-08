package um.tesoreria.core.hexagonal.cobranza.pagoManual.infrastructure.web.dto;

public record ResultadoEliminacionPagoResponse(
        Long chequeraPagoId,
        boolean eliminado) {
}
