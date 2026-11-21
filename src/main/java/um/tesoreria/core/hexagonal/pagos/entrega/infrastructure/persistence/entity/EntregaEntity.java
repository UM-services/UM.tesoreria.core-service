package um.tesoreria.core.hexagonal.pagos.entrega.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.model.Auditable;

import java.time.OffsetDateTime;

/**
 * Réplica de la entidad legacy {@code Entrega} (tabla {@code entrega}).
 * Se mantienen los mismos nombres lógicos y columnas físicas para que Hibernate
 * no detecte dos mapeos distintos sobre la misma tabla.
 */
@Getter
@Setter
@Entity
@Table(name = "Entrega")
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntregaEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "noe_id")
    private Long entregaId;

    @Column(name = "noe_fecha")
    private OffsetDateTime fecha;

    @Column(name = "noe_ubi_id")
    private Integer ubicacionId;

    @Column(name = "noe_recibe")
    @Builder.Default
    private String recibe = "";

    @Column(name = "noe_observacion")
    @Builder.Default
    private String observacion = "";

    @Column(name = "noe_mco_fecha")
    private OffsetDateTime fechaContable;

    @Column(name = "noe_mco_nrocomp")
    @Builder.Default
    private Integer ordenContable = 0;

    @Column(name = "noe_anulada")
    @Builder.Default
    private Byte anulada = 0;

    @Column(name = "noe_tipo")
    @Builder.Default
    private String tipo = "";

    private Long trackId;
}
