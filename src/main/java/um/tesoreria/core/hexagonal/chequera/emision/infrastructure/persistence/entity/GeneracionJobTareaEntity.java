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
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionTareaEstado;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "chequera_generacion_job_tarea")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneracionJobTareaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tarea_id")
    private Long tareaId;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "chequera_id")
    private Long chequeraId;

    @Column(name = "persona_id", nullable = false)
    private BigDecimal personaId;

    @Column(name = "documento_id", nullable = false)
    private Integer documentoId;

    @Column(name = "facultad_id", nullable = false)
    private Integer facultadId;

    @Column(name = "tipo_chequera_id", nullable = false)
    private Integer tipoChequeraId;

    @Column(name = "chequera_serie_id")
    private Long chequeraSerieId;

    @Column(name = "curso_id")
    private Integer cursoId;

    @Column(name = "persona_nombre", length = 120)
    private String personaNombre;

    @Column(name = "borrador_id")
    private Long borradorId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 30)
    private GeneracionTareaEstado estado;

    @Column(name = "codigo", length = 60)
    private String codigo;

    @Column(name = "mensaje", length = 500)
    private String mensaje;

    @Column(name = "finalizado")
    private OffsetDateTime finalizado;
}
