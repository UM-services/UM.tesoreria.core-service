package um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJob;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobTarea;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioTareaEstado;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface EnvioJobRepository {

    EnvioJob create(EnvioJob job, List<EnvioJobTarea> tareas);

    Optional<EnvioJob> findByJobId(Long jobId);

    Optional<EnvioJob> findWithLockByJobId(Long jobId);

    Optional<EnvioJob> claimNextPending();

    /** Job activo (PENDIENTE o EN_PROCESO), si existe: el worker procesa uno por vez. */
    Optional<EnvioJob> findActivo();

    List<EnvioJob> findPendientes();

    List<EnvioJobTarea> findAllTareasByJobId(Long jobId);

    Optional<EnvioJobTarea> findTareaById(Long tareaId);

    List<EnvioJobTarea> findPendingTareasByJobId(Long jobId);

    long countTareasByJobIdAndEstado(Long jobId, EnvioTareaEstado estado);

    long countTareasByJobIdAndEstadoIn(Long jobId, List<EnvioTareaEstado> estados);

    EnvioJob update(EnvioJob job);

    EnvioJobTarea updateTarea(EnvioJobTarea tarea);

    /**
     * Acuse del sender: pasa a ENVIADA o ERROR_SMTP la tarea ENCOLADA más reciente de esa chequera.
     * Devuelve la tarea actualizada (vacío si no había ninguna esperando acuse).
     */
    Optional<EnvioJobTarea> registrarAcuse(Long chequeraId, boolean enviado, String error);

    /**
     * Acuse de una solicitud concreta: actualiza la tarea de ese job y chequera. No degrada una
     * tarea ya ENVIADA ante un acuse tardío de error.
     */
    Optional<EnvioJobTarea> registrarAcuse(Long jobId, Long chequeraId, EnvioTareaEstado estado,
                                           boolean enviado, String mensaje);

    /** Vuelve a PENDIENTE las tareas del job en los estados indicados (reintento). */
    int reactivarTareas(Long jobId, List<EnvioTareaEstado> estados);

    /** Devuelve a PENDIENTE los jobs EN_PROCESO cuyo heartbeat quedó viejo. */
    int requeueStale(OffsetDateTime heartbeatLimite);

    int deleteFinalizedBefore(OffsetDateTime finalizadoLimite);
}
