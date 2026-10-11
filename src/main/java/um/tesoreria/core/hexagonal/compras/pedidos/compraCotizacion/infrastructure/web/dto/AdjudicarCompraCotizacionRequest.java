package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto;

import java.math.BigDecimal;

/** Adjudicación de un proceso recomendado: decisión, oferta elegida, monto, autoridad y límite. */
public record AdjudicarCompraCotizacionRequest(
        String decision,
        Long compraCotizacionProveedorId,
        Integer proveedorAdjudicadoId,
        BigDecimal monto,
        String fundamento,
        Long autoridadUsuarioId,
        BigDecimal limiteAplicado) {
}
