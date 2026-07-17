package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model;

import lombok.*;

import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.geografica.domain.model.Geografica;
import um.tesoreria.core.kotlin.model.ProveedorArticulo;
import um.tesoreria.core.kotlin.model.ProveedorPago;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorMovimiento {
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

    private Comprobante comprobante;
    private Proveedor proveedor;
    private Geografica geografica;
    private List<ProveedorArticulo> proveedorArticulos;
    private List<ProveedorPago> ordenPagos;
}
