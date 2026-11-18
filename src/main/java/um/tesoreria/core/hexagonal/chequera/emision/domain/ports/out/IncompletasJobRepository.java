package um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasEtapa;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasTareaEstado;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface IncompletasJobRepository {

    IncompletasJob create(IncompletasJob job, List<IncompletasJobTarea> tareas);

    Optional<IncompletasJob> findByJobId(Long jobId);

    Optional<IncompletasJob> findWithLockByJobId(Long jobId);

    Optional<IncompletasJob> claimNextPending();

    List<IncompletasJobTarea> findAllTareasByJobId(Long jobId);

    List<IncompletasJobTarea> findPendingTareasByJobId(Long jobId);

    /** Tareas de un job en cualquiera de los estados indicados (p. ej. borradores listos para emitir). */
    List<IncompletasJobTarea> findTareasByJobIdAndEstadoIn(Long jobId, List<IncompletasTareaEstado> estados);

    long countTareasByJobIdAndEstado(Long jobId, IncompletasTareaEstado estado);

    long countTareasByJobIdAndEstadoIn(Long jobId, List<IncompletasTareaEstado> estados);

    long countByEstado(IncompletasJobEstado estado);

    IncompletasJob update(IncompletasJob job);

    IncompletasJobTarea updateTarea(IncompletasJobTarea tarea);

    /** Devuelve a {@code PENDIENTE} los jobs EN_PROCESO cuyo heartbeat quedó viejo. */
    int requeueStale(OffsetDateTime heartbeatLimite);

    int deleteFinalizedBefore(OffsetDateTime finalizadoLimite);

    /** Job activo (PENDIENTE o EN_PROCESO) de una etapa, si existe. */
    Optional<IncompletasJob> findActivoByEtapa(IncompletasEtapa etapa);

    List<IncompletasJob> findPendientes();
}
