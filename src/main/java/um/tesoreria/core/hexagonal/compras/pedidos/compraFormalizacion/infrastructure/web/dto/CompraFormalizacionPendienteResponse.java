package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/** Adjudicación pendiente de formalizar (bandeja de la Orden de Compra). */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraFormalizacionPendienteResponse {

    private Long compraCotizacionId;
    private String numero;
    private Integer ejercicioId;
    private Long compraAdjudicacionId;
    private Long compraCotizacionProveedorId;
    private Integer proveedorId;
    private BigDecimal monto;
    private String moneda;
    private String condicionPago;
    private String plazoEntrega;
    private String garantia;
    private List<CompraFormalizacionItemResponse> items;

}
