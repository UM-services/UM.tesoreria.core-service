package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity;

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
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionEtapa;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobEstado;
import um.tesoreria.core.model.Auditable;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "chequera_generacion_job")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneracionJobEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long jobId;

    @Enumerated(EnumType.STRING)
    @Column(name = "etapa", length = 20, nullable = false)
    private GeneracionEtapa etapa;

    @Column(name = "job_origen_id")
    private Long jobOrigenId;

    @Column(name = "lectivo_id", nullable = false)
    private Integer lectivoId;

    @Column(name = "facultad_id", nullable = false)
    private Integer facultadId;

    @Column(name = "geografica_id", nullable = false)
    private Integer geograficaId;

    @Column(name = "clase_chequera_id")
    private Integer claseChequeraId;

    @Column(name = "tipo_chequera_id")
    private Integer tipoChequeraId;

    @Column(name = "solo_no_enviadas", nullable = false)
    private boolean soloNoEnviadas;

    @Column(name = "forzar_deuda", nullable = false)
    private boolean forzarDeuda;

    @Column(name = "motivo_override", length = 255)
    private String motivoOverride;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private GeneracionJobEstado estado;

    @Builder.Default
    @Column(name = "total_tareas")
    private int totalTareas = 0;

    @Builder.Default
    @Column(name = "tareas_ok")
    private int tareasOk = 0;

    @Builder.Default
    @Column(name = "tareas_omitidas")
    private int tareasOmitidas = 0;

    @Builder.Default
    @Column(name = "tareas_error")
    private int tareasError = 0;

    @Builder.Default
    @Column(name = "tareas_pendientes")
    private int tareasPendientes = 0;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "usuario_login", length = 50)
    private String usuarioLogin;

    @Column(name = "heartbeat")
    private OffsetDateTime heartbeat;

    @Column(name = "iniciado")
    private OffsetDateTime iniciado;

    @Column(name = "finalizado")
    private OffsetDateTime finalizado;

    @Column(name = "mensaje", length = 500)
    private String mensaje;

    @Column(name = "ultima_chequera_id")
    private Long ultimaChequeraId;
}
