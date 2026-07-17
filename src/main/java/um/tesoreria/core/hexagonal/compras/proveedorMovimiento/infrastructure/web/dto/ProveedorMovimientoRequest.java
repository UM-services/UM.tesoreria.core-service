package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorMovimientoRequest {
    private Integer proveedorId;
    private String nombreBeneficiario;
    private Integer comprobanteId;
    private OffsetDateTime fechaComprobante;
    private OffsetDateTime fechaVencimiento;
    private Integer prefijo;
    private Long numeroComprobante;
    private BigDecimal netoSinDescuento;
    private BigDecimal descuento;
    private BigDecimal neto;
    private BigDecimal importe;
    private BigDecimal cancelado;
    private OffsetDateTime fechaContable;
    private Integer ordenContable;
    private String concepto;
    private OffsetDateTime fechaAnulacion;
    private Byte conCargo;
    private Byte solicitaFactura;
    private Integer geograficaId;
}
