package um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model;

public record ResultadoEliminacionPago(
        Long chequeraPagoId,
        boolean eliminado) {
}
