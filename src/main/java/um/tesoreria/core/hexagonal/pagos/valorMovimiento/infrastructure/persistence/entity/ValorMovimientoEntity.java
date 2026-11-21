package um.tesoreria.core.hexagonal.pagos.valorMovimiento.infrastructure.persistence.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
 * Mapeo propio de {@code valores}. Replica nombres lógicos y columnas de {@code ValorMovimiento.kt};
 * las columnas de anulación no se mapean porque el alta no las usa.
 */
@Getter
@Setter
@Entity
@Table(name = "valores")
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValorMovimientoEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "val_id")
    private Long valorMovimientoId;

    @Column(name = "val_tiv_id")
    private Integer valorId;

    @Column(name = "val_fechaemision")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaEmision;

    @Column(name = "val_numero")
    @Builder.Default
    private Long numero = 0L;

    @Column(name = "val_cuentaorigen")
    private Long bancariaIdOrigen;

    @Column(name = "val_fechaefectivizacion")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaEfectivizacion;

    @Column(name = "val_importe")
    @Builder.Default
    private BigDecimal importe = BigDecimal.ZERO;

    @Column(name = "val_estado")
    private String estado;

    @Column(name = "val_nombretitular")
    @Builder.Default
    private String nombreTitular = "";

    @Column(name = "val_cuittitular")
    @Builder.Default
    private String cuitTitular = "";

    @Column(name = "val_reemplazo")
    @Builder.Default
    private Long reemplazo = 0L;

    @Column(name = "val_fechaasiento")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXX", timezone = "UTC")
    private OffsetDateTime fechaContable;

    @Column(name = "val_numeroasiento")
    private Integer ordenContable;

    @Column(name = "val_letras")
    @Builder.Default
    private String letras = "";
}
