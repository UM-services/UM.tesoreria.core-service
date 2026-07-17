package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaEntity;
import um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.entity.ProveedorEntity;
import um.tesoreria.core.hexagonal.comprobante.infrastructure.persistence.entity.ComprobanteEntity;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.entity.ProveedorMovimientoEntity;
import um.tesoreria.core.hexagonal.track.infrastructure.persistence.entity.TrackEntity;
import um.tesoreria.core.model.Auditable;
import um.tesoreria.core.util.Jsonifyable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(
    name = "movcon",
    uniqueConstraints = @UniqueConstraint(columnNames = {"mco_fecha", "mco_nrocomp", "mco_item"})
)
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaMovimientoEntity extends Auditable implements Jsonifyable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long cuentaMovimientoId;

    @Column(name = "mco_fecha")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaContable;

    @Column(name = "mco_nrocomp")
    @Builder.Default
    private int ordenContable = 0;

    @Column(name = "mco_item")
    @Builder.Default
    private int item = 0;

    @Column(name = "mco_pla_cuenta")
    private BigDecimal numeroCuenta;

    @Column(name = "mco_debita")
    @Builder.Default
    private byte debita = 0;

    @Column(name = "mco_tco_id")
    private Integer comprobanteId;

    @Column(name = "mco_concepto")
    @Builder.Default
    private String concepto = "";

    @Column(name = "mco_importe")
    @Builder.Default
    private BigDecimal importe = BigDecimal.ZERO;

    @Column(name = "mco_prv_id")
    private Integer proveedorId;

    @Column(name = "mco_nroanulado")
    @Builder.Default
    private int numeroAnulado = 0;

    @Builder.Default
    private int version = 0;

    @Column(name = "proveedormovimiento_id")
    private Long proveedorMovimientoId;

    @Column(name = "proveedormovimiento_id_orden_pago")
    private Long proveedorMovimientoIdOrdenPago;

    @Builder.Default
    private byte apertura = 0;

    private Long trackId;

    @OneToOne(optional = true)
    @JoinColumn(name = "mco_pla_cuenta", insertable = false, updatable = false)
    private CuentaEntity cuenta;

    @OneToOne(optional = true)
    @JoinColumn(name = "mco_prv_id", insertable = false, updatable = false)
    private ProveedorEntity proveedor;

    @OneToOne(optional = true)
    @JoinColumn(name = "mco_tco_id", insertable = false, updatable = false)
    private ComprobanteEntity comprobante;

    @OneToOne(optional = true)
    @JoinColumn(name = "proveedormovimiento_id", insertable = false, updatable = false)
    private ProveedorMovimientoEntity proveedorMovimiento;

    @OneToOne(optional = true)
    @JoinColumn(name = "proveedormovimiento_id_orden_pago", insertable = false, updatable = false)
    private ProveedorMovimientoEntity ordenPago;

    @OneToOne(optional = true)
    @JoinColumn(name = "trackId", insertable = false, updatable = false)
    private TrackEntity track;

}
