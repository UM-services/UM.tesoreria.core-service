package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobEstado;
import um.tesoreria.core.model.Auditable;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "contable_asiento_ingreso_job")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsientoIngresoJobEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long jobId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private AsientoIngresoJobEstado estado;

    @Column(name = "fecha_desde")
    private OffsetDateTime fechaDesde;

    @Column(name = "fecha_hasta")
    private OffsetDateTime fechaHasta;

    @Builder.Default
    @Column(name = "total_tareas")
    private int totalTareas = 0;

    @Builder.Default
    @Column(name = "tareas_ok")
    private int tareasOk = 0;

    @Builder.Default
    @Column(name = "tareas_error")
    private int tareasError = 0;

    @Builder.Default
    @Column(name = "tareas_sin_mov")
    private int tareasSinMov = 0;

    @Builder.Default
    @Column(name = "tareas_pendientes")
    private int tareasPendientes = 0;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "heartbeat")
    private OffsetDateTime heartbeat;

    @Column(name = "iniciado")
    private OffsetDateTime iniciado;

    @Column(name = "finalizado")
    private OffsetDateTime finalizado;

    @Column(name = "mensaje", length = 500)
    private String mensaje;

    @Column(name = "fecha_actual")
    private OffsetDateTime fechaActual;

    @Column(name = "tipo_pago_actual")
    private Integer tipoPagoActual;

}
