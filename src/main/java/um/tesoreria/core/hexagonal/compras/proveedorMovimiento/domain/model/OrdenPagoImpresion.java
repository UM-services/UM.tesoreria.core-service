package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import um.tesoreria.core.kotlin.model.ValorMovimiento;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Orden de pago lista para imprimir (reimpresión de comprobantes del legacy,
 * {@code OrdenPago.rpt}): cabecera, proveedor, comprobantes que cancela, valores
 * y traza contable consolidada por cuenta.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenPagoImpresion {

    private Long proveedorMovimientoId;
    private String comprobanteDescripcion;
    /** {@code 0000-00000000}. */
    private String numero;
    private Integer prefijo;
    private Long numeroComprobante;
    private OffsetDateTime fechaComprobante;
    private OffsetDateTime fechaAnulacion;
    private String concepto;
    private String nombreBeneficiario;
    private Integer proveedorId;
    private String razonSocial;
    private String domicilio;
    private String cuit;

    @Builder.Default
    private List<ComprobanteImpreso> comprobantes = new ArrayList<>();
    private BigDecimal totalComprobantes;
    @Builder.Default
    private List<ValorMovimiento> valores = new ArrayList<>();
    private BigDecimal totalValores;
    @Builder.Default
    private List<TrazaLinea> traza = new ArrayList<>();
    private BigDecimal totalDebe;
    private BigDecimal totalHaber;

    /**
     * Comprobante cancelado por la orden ({@code movprov_detallecomprobantes}).
     * {@code importe} es el total del comprobante; {@code importeAplicado}, lo
     * que esta orden aplicó sobre él ({@code opc_importe}).
     */
    public record ComprobanteImpreso(Long proveedorMovimientoId,
                                     String comprobanteDescripcion,
                                     Integer prefijo,
                                     Long numeroComprobante,
                                     OffsetDateTime fechaComprobante,
                                     BigDecimal importe,
                                     BigDecimal importeAplicado) {
    }

    /** Línea de la traza contable consolidada: un solo lado (debe o haber) por cuenta. */
    public record TrazaLinea(BigDecimal numeroCuenta,
                             String nombreCuenta,
                             BigDecimal debe,
                             BigDecimal haber) {
    }
}