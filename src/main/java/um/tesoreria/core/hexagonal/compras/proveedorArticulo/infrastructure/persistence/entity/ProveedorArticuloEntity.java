package um.tesoreria.core.hexagonal.compras.proveedorArticulo.infrastructure.persistence.entity;

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

/**
 * Mapeo propio de {@code movprov_detallefactura}. Las columnas y los nombres lógicos replican el
 * mapeo de {@code ProveedorArticulo.kt}: Hibernate rechaza dos entidades sobre la misma tabla con
 * nombres lógicos distintos para una misma columna.
 */
@Getter
@Setter
@Entity
@Table(name = "movprov_detallefactura")
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorArticuloEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fad_id")
    private Long proveedorArticuloId;

    @Column(name = "fad_mvp_id")
    private Long proveedorMovimientoId;

    @Column(name = "fad_orden")
    private Integer orden;

    @Column(name = "fad_art_id")
    private Long articuloId;

    @Column(name = "fad_cantidad")
    @Builder.Default
    private BigDecimal cantidad = BigDecimal.ZERO;

    @Column(name = "fad_concepto")
    @Builder.Default
    private String concepto = "";

    @Column(name = "fad_preciounitario")
    @Builder.Default
    private BigDecimal precioUnitario = BigDecimal.ZERO;

    @Column(name = "fad_preciofinal")
    @Builder.Default
    private BigDecimal precioFinal = BigDecimal.ZERO;

    @Column(name = "fad_entregado")
    @Builder.Default
    private BigDecimal entregado = BigDecimal.ZERO;

    @Column(name = "fad_asignado")
    @Builder.Default
    private BigDecimal asignado = BigDecimal.ZERO;
}
