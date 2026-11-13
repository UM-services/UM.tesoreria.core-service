package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraImpresionResponse {

    private Long proveedorMovimientoId;
    private String comprobanteDescripcion;
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
    private List<Detalle> detalle;
    private OffsetDateTime fechaContable;
    private Integer ordenContable;
    private List<Asiento> asiento;
    private BigDecimal totalDebe;
    private BigDecimal totalHaber;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detalle {
        private Integer orden;
        private String articulo;
        private String concepto;
        private BigDecimal cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal precioFinal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Asiento {
        private BigDecimal numeroCuenta;
        private String nombreCuenta;
        private BigDecimal debe;
        private BigDecimal haber;
    }
}