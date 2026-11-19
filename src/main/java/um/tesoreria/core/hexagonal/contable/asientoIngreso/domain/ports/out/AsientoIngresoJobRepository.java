package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJob;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobTarea;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobTareaEstado;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface AsientoIngresoJobRepository {

    AsientoIngresoJob create(AsientoIngresoJob job, List<AsientoIngresoJobTarea> tareas);

    Optional<AsientoIngresoJob> findByJobId(Long jobId);

    Optional<AsientoIngresoJob> findWithLockByJobId(Long jobId);

    Optional<AsientoIngresoJob> claimNextPending();

    List<AsientoIngresoJobTarea> findAllTareasByJobId(Long jobId);

    List<AsientoIngresoJobTarea> findPendingTareasByJobId(Long jobId);

    long countTareasByJobIdAndEstado(Long jobId, AsientoIngresoJobTareaEstado estado);

    AsientoIngresoJob update(AsientoIngresoJob job);

    AsientoIngresoJobTarea updateTarea(AsientoIngresoJobTarea tarea);

    /** Devuelve a {@code PENDIENTE} los jobs EN_PROCESO cuyo heartbeat quedó viejo. */
    int requeueStale(OffsetDateTime heartbeatLimite);

    int deleteFinalizedBefore(OffsetDateTime finalizadoLimite);

}
