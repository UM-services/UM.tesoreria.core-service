package um.tesoreria.core.hexagonal.facturaPendiente.domain.model;

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
public class FacturaPendiente {
    private Long proveedorMovimientoId;
    private String razonSocial;
    private String cuit;
    private String cbu;
    private OffsetDateTime fechaComprobante;
    private OffsetDateTime fechaVencimiento;
    private String observaciones;
    private String comprobante;
    private Byte debita;
    private Integer prefijo;
    private Long numeroComprobante;
    private BigDecimal importeFactura;
    private BigDecimal importePagado;
}
