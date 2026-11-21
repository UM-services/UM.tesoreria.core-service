package um.tesoreria.core.hexagonal.pagos.entrega.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;

/**
 * Réplica de la entidad legacy {@code EntregaDetalle} (tabla {@code entrega_detalle}).
 * Se mantienen los mismos nombres lógicos y columnas físicas para que Hibernate
 * no detecte dos mapeos distintos sobre la misma tabla.
 */
@Getter
@Setter
@Entity
@Table(name = "EntregaDetalle")
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntregaDetalleEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ned_id")
    private Long entregaDetalleId;

    @Column(name = "ned_noe_id")
    private Long entregaId;

    @Column(name = "ned_art_id")
    private Long articuloId;

    @Column(name = "ned_fad_mvp_id")
    private Long proveedorMovimientoId;

    @Column(name = "ned_fad_orden")
    @Builder.Default
    private Integer orden = 0;

    @Column(name = "ned_concepto")
    @Builder.Default
    private String concepto = "";

    @Column(name = "ned_cantidad")
    @Builder.Default
    private BigDecimal cantidad = BigDecimal.ZERO;

    private Long trackId;

    private Long proveedorArticuloId;
}
