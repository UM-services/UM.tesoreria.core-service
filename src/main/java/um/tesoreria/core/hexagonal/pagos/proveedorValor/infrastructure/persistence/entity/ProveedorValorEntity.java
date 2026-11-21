package um.tesoreria.core.hexagonal.pagos.proveedorValor.infrastructure.persistence.entity;

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

/**
 * Mapeo propio de {@code movprov_detallevalores}. Las columnas y los nombres lógicos replican el
 * mapeo de {@code ProveedorValor.kt}: Hibernate rechaza dos entidades sobre la misma tabla con
 * nombres lógicos distintos para una misma columna.
 */
@Getter
@Setter
@Entity
@Table(name = "movprov_detallevalores")
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorValorEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opv_id")
    private Long proveedorValorId;

    @Column(name = "opv_orp_id")
    private Long proveedorMovimientoId;

    @Column(name = "opv_orden")
    @Builder.Default
    private Integer orden = 0;

    @Column(name = "opv_val_id")
    private Long valorMovimientoId;
}
