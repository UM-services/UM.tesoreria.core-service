package um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionEtapa;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionTareaEstado;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface GeneracionJobRepository {

    GeneracionJob create(GeneracionJob job, List<GeneracionJobTarea> tareas);

    Optional<GeneracionJob> findByJobId(Long jobId);

    Optional<GeneracionJob> findWithLockByJobId(Long jobId);

    Optional<GeneracionJob> claimNextPending();

    List<GeneracionJobTarea> findAllTareasByJobId(Long jobId);

    List<GeneracionJobTarea> findPendingTareasByJobId(Long jobId);

    List<GeneracionJobTarea> findTareasByJobIdAndEstadoIn(Long jobId, List<GeneracionTareaEstado> estados);

    long countTareasByJobIdAndEstado(Long jobId, GeneracionTareaEstado estado);

    long countTareasByJobIdAndEstadoIn(Long jobId, List<GeneracionTareaEstado> estados);

    GeneracionJob update(GeneracionJob job);

    GeneracionJobTarea updateTarea(GeneracionJobTarea tarea);

    /** Devuelve a {@code PENDIENTE} los jobs EN_PROCESO cuyo heartbeat quedó viejo. */
    int requeueStale(OffsetDateTime heartbeatLimite);

    int deleteFinalizedBefore(OffsetDateTime finalizadoLimite);

    /** Job activo (PENDIENTE o EN_PROCESO) de una etapa, si existe. */
    Optional<GeneracionJob> findActivoByEtapa(GeneracionEtapa etapa);

    List<GeneracionJob> findPendientes();
}
