package um.tesoreria.core.hexagonal.pagos.proveedorPago.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Mapeo propio de {@code movprov_detallecomprobantes}. Las columnas y los nombres lógicos replican
 * el mapeo de {@code ProveedorPago.kt}: Hibernate rechaza dos entidades sobre la misma tabla con
 * nombres lógicos distintos para una misma columna.
 */
@Getter
@Setter
@Entity
@Table(name = "movprov_detallecomprobantes")
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorPagoEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opc_id")
    private Long proveedorPagoId;

    @Column(name = "opc_orp_id")
    private Long proveedorMovimientoIdPago;

    @Column(name = "opc_mvp_id")
    private Long proveedorMovimientoIdFactura;

    @Column(name = "opc_importe")
    @Builder.Default
    private BigDecimal importeAplicado = BigDecimal.ZERO;

    private OffsetDateTime fechaPago;

    @Builder.Default
    private Byte trazaContable = 0;
}
