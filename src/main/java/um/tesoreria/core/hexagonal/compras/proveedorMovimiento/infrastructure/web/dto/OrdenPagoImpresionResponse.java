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
public class OrdenPagoImpresionResponse {

    private Long proveedorMovimientoId;
    private String comprobanteDescripcion;
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
    private List<Comprobante> comprobantes;
    private BigDecimal totalComprobantes;
    private List<ProveedorValorResponse> valores;
    private BigDecimal totalValores;
    private List<Traza> traza;
    private BigDecimal totalDebe;
    private BigDecimal totalHaber;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comprobante {
        private Long proveedorMovimientoId;
        private String comprobanteDescripcion;
        private Integer prefijo;
        private Long numeroComprobante;
        private OffsetDateTime fechaComprobante;
        private BigDecimal importe;
        private BigDecimal importeAplicado;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Traza {
        private BigDecimal numeroCuenta;
        private String nombreCuenta;
        private BigDecimal debe;
        private BigDecimal haber;
    }
}