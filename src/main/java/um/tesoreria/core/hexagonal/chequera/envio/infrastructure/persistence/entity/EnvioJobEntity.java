package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.entity;

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
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobEstado;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioOperacion;
import um.tesoreria.core.model.Auditable;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@DynamicUpdate
@Table(name = "chequera_envio_job")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvioJobEntity extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long jobId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operacion", length = 20, nullable = false)
    private EnvioOperacion operacion;

    @Column(name = "lectivo_id", nullable = false)
    private Integer lectivoId;

    @Column(name = "facultad_id", nullable = false)
    private Integer facultadId;

    @Column(name = "geografica_id", nullable = false)
    private Integer geograficaId;

    @Column(name = "solo_no_enviadas", nullable = false)
    private boolean soloNoEnviadas;

    @Column(name = "codigo_barras", nullable = false)
    private boolean codigoBarras;

    /**
     * Columna legada del envío con retención por deuda. La política se retiró: no se lee ni se
     * escribe desde el dominio y los jobs nuevos conservan el valor histórico por defecto. Se
     * mantiene mapeada para no exigir DDL sobre una tabla existente.
     */
    @Builder.Default
    @Column(name = "umbral_cuotas_deuda", nullable = false)
    private int umbralCuotasDeuda = 3;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EnvioJobEstado estado;

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
