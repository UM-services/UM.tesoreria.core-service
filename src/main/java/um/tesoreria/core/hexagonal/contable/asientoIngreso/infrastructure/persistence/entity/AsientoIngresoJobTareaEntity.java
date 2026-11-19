package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobTareaEstado;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "contable_asiento_ingreso_job_tarea",
        uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "fecha_contable", "tipo_pago_id"}))
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsientoIngresoJobTareaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tarea_id")
    private Long tareaId;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "fecha_contable", nullable = false)
    private OffsetDateTime fechaContable;

    @Column(name = "tipo_pago_id", nullable = false)
    private Integer tipoPagoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private AsientoIngresoJobTareaEstado estado;

    @Column(name = "orden_contable")
    private Integer ordenContable;

    @Column(name = "total_debe")
    private BigDecimal totalDebe;

    @Column(name = "total_haber")
    private BigDecimal totalHaber;

    @Column(name = "faltantes_json", columnDefinition = "TEXT")
    private String faltantesJson;

    @Column(name = "error_codigo", length = 60)
    private String errorCodigo;

    @Column(name = "error_mensaje", length = 500)
    private String errorMensaje;

    @Column(name = "iniciado")
    private OffsetDateTime iniciado;

    @Column(name = "finalizado")
    private OffsetDateTime finalizado;

}
