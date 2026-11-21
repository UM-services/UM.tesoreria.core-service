package um.tesoreria.core.hexagonal.contable.bancoMovimiento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import um.tesoreria.core.model.Auditable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Réplica de la entidad legacy {@code BancoMovimiento} (tabla {@code libro_banco}).
 * Se mantienen los mismos nombres lógicos y columnas físicas para que Hibernate
 * no detecte dos mapeos distintos sobre la misma tabla.
 */
@Getter
@Setter
@Entity
@Table(name = "libro_banco")
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BancoMovimientoEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lib_id")
    private Long bancoMovimientoId;

    @Column(name = "lib_cub_id")
    private Long bancariaId;

    @Column(name = "lib_fecha")
    private OffsetDateTime fecha;

    @Column(name = "lib_orden")
    @Builder.Default
    private Long orden = 0L;

    @Column(name = "lib_tipo")
    private String tipo;

    @Column(name = "lib_nrocomprobante")
    private String numeroComprobante;

    @Column(name = "lib_concepto")
    private String concepto;

    @Column(name = "lib_importe")
    private BigDecimal importe;

    @Column(name = "lib_cuentacontable")
    private BigDecimal numeroCuenta;

    @Column(name = "lib_debita")
    @Builder.Default
    private Byte debita = 0;

    @Column(name = "lib_fechaconciliacion")
    private OffsetDateTime fechaConciliacion;

    @Column(name = "lib_anulado")
    @Builder.Default
    private Byte anulado = 0;

    @Column(name = "lib_fechaasiento")
    private OffsetDateTime fechaContable;

    @Column(name = "lib_numeroasiento")
    private Integer ordenContable;

    @Column(name = "lib_val_id")
    private Long valorMovimientoId;
}
