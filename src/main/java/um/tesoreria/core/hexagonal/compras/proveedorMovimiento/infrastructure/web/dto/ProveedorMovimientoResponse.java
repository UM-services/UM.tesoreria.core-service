package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.comprobante.infrastructure.web.dto.ComprobanteResponse;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.dto.ProveedorResponse;
import um.tesoreria.core.hexagonal.geografica.infrastructure.web.dto.GeograficaResponse;
import um.tesoreria.core.kotlin.model.ProveedorArticulo;
import um.tesoreria.core.kotlin.model.ProveedorPago;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorMovimientoResponse {
    private Long proveedorMovimientoId;
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
    private ComprobanteResponse comprobante;
    private ProveedorResponse proveedor;
    private GeograficaResponse geografica;
    private List<ProveedorArticulo> proveedorArticulos;
    private List<ProveedorPago> ordenPagos;
}
