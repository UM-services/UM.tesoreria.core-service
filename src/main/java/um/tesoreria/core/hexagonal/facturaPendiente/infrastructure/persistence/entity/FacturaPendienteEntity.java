package um.tesoreria.core.hexagonal.facturaPendiente.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Immutable
@NoArgsConstructor
@AllArgsConstructor
public class FacturaPendienteEntity {
    @Id
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
