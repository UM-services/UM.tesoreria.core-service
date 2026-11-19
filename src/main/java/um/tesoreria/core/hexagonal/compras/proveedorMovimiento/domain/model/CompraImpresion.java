package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Factura o nota de crédito de compras lista para imprimir (reimpresión de comprobantes del
 * legacy, {@code rptIngresoCompra.rpt}, «Ingreso de Comprobante de Compras»): cabecera,
 * proveedor, detalle de artículos y asiento contable. Los importes son los guardados, con su
 * signo (una nota de crédito sale negativa).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraImpresion {

    private Long proveedorMovimientoId;
    /** Descripción del comprobante: «Factura B», «N. Credito B», etc. */
    private String comprobanteDescripcion;
    /** {@code 0000-00000000}. */
    private String numero;
    private Integer prefijo;
    private Long numeroComprobante;
    private OffsetDateTime fechaComprobante;
    private OffsetDateTime fechaAnulacion;
    private String concepto;
    private Integer proveedorId;
    private String razonSocial;
    private String domicilio;
    private String cuit;
    private BigDecimal importe;

    @Builder.Default
    private List<DetalleImpreso> detalle = new ArrayList<>();

    /** Fecha y orden del asiento del comprobante ({@code MvP_FechaReg} y {@code MvP_NroComp}). */
    private OffsetDateTime fechaContable;
    private Integer ordenContable;
    @Builder.Default
    private List<AsientoLinea> asiento = new ArrayList<>();
    private BigDecimal totalDebe;
    private BigDecimal totalHaber;

    /**
     * Renglón del detalle ({@code movprov_detalleFactura}). {@code articulo} es el nombre del
     * artículo y {@code concepto} el texto libre del renglón; {@code precioFinal} es el subtotal.
     */
    public record DetalleImpreso(Integer orden,
                                 String articulo,
                                 String concepto,
                                 BigDecimal cantidad,
                                 BigDecimal precioUnitario,
                                 BigDecimal precioFinal) {
    }

    /** Línea del asiento contable: un solo lado (debe o haber) por línea. */
    public record AsientoLinea(BigDecimal numeroCuenta,
                               String nombreCuenta,
                               BigDecimal debe,
                               BigDecimal haber) {
    }
}