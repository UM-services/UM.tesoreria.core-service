package um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoAccion;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJob;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobEstado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobTarea;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobTareaEstado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoResultado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.AsientoIngresoJobRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Operaciones del job que usa el worker: reclamar, registrar el resultado de cada tarea y cerrar.
 * Cada actualización es una transacción corta; el lock del job nunca se retiene durante el proceso.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AsientoIngresoJobService {

    private final AsientoIngresoJobRepository repository;
    private final TransactionOperations transaction;

    public Optional<AsientoIngresoJob> reclamarSiguiente() {
        return transaction.execute(status -> repository.claimNextPending().map(job -> {
            OffsetDateTime ahora = ahora();
            job.setEstado(AsientoIngresoJobEstado.EN_PROCESO);
            job.setHeartbeat(ahora);
            if (job.getIniciado() == null) {
                job.setIniciado(ahora);
            }
            return repository.update(job);
        }));
    }

    public List<AsientoIngresoJobTarea> tareasPendientes(Long jobId) {
        return repository.findPendingTareasByJobId(jobId);
    }

    public boolean estaCancelado(Long jobId) {
        return repository.findByJobId(jobId)
                .map(job -> job.getEstado() == AsientoIngresoJobEstado.CANCELADO || job.getEstado() == AsientoIngresoJobEstado.ERROR)
                .orElse(true);
    }

    public void registrarTareaOk(Long jobId, AsientoIngresoJobTarea tarea, AsientoIngresoResultado resultado) {
        transaction.executeWithoutResult(status -> {
            tarea.setEstado(resultado.getAccion() == AsientoIngresoAccion.SIN_MOVIMIENTOS
                    ? AsientoIngresoJobTareaEstado.SIN_MOVIMIENTOS
                    : AsientoIngresoJobTareaEstado.OK);
            tarea.setOrdenContable(resultado.getResumen().getOrdenContable());
            tarea.setTotalDebe(resultado.getResumen().getTotalDebe());
            tarea.setTotalHaber(resultado.getResumen().getTotalHaber());
            tarea.setFaltantes(resultado.getFaltantes());
            tarea.setFinalizado(ahora());
            repository.updateTarea(tarea);
            actualizarJob(jobId, tarea);
        });
    }

    public void registrarTareaError(Long jobId, AsientoIngresoJobTarea tarea, String codigo, String mensaje) {
        transaction.executeWithoutResult(status -> {
            tarea.setEstado(AsientoIngresoJobTareaEstado.ERROR);
            tarea.setErrorCodigo(recortar(codigo, 60));
            tarea.setErrorMensaje(recortar(mensaje, 500));
            tarea.setFinalizado(ahora());
            repository.updateTarea(tarea);
            actualizarJob(jobId, tarea);
        });
    }

    public void completar(Long jobId) {
        transaction.executeWithoutResult(status -> repository.findWithLockByJobId(jobId).ifPresent(job -> {
            job.setEstado(AsientoIngresoJobEstado.COMPLETADO);
            job.setFinalizado(ahora());
            job.setHeartbeat(ahora());
            job.setTareasPendientes((int) repository.countTareasByJobIdAndEstado(
                    jobId, AsientoIngresoJobTareaEstado.PENDIENTE));
            repository.update(job);
        }));
    }

    public void marcarCancelado(Long jobId) {
        transaction.executeWithoutResult(status -> repository.findWithLockByJobId(jobId).ifPresent(job -> {
            job.setEstado(AsientoIngresoJobEstado.CANCELADO);
            job.setFinalizado(ahora());
            job.setHeartbeat(ahora());
            repository.update(job);
        }));
    }

    public void marcarError(Long jobId, String mensaje) {
        transaction.executeWithoutResult(status -> repository.findWithLockByJobId(jobId).ifPresent(job -> {
            job.setEstado(AsientoIngresoJobEstado.ERROR);
            job.setMensaje(recortar(mensaje, 500));
            job.setFinalizado(ahora());
            job.setHeartbeat(ahora());
            repository.update(job);
        }));
    }

    public int recuperarVencidos(long minutos) {
        Integer recuperados = transaction.execute(status ->
                repository.requeueStale(ahora().minusMinutes(minutos)));
        int total = Objects.requireNonNullElse(recuperados, 0);
        if (total > 0) {
            log.warn("Asiento ingreso: {} job(s) reencolados por heartbeat vencido", total);
        }
        return total;
    }

    public int purgar(long dias) {
        Integer purgados = transaction.execute(status ->
                repository.deleteFinalizedBefore(ahora().minusDays(dias)));
        int total = Objects.requireNonNullElse(purgados, 0);
        if (total > 0) {
            log.info("Asiento ingreso: {} job(s) finalizados eliminados", total);
        }
        return total;
    }

    private void actualizarJob(Long jobId, AsientoIngresoJobTarea tarea) {
        repository.findWithLockByJobId(jobId).ifPresent(job -> {
            job.setTareasOk((int) repository.countTareasByJobIdAndEstado(jobId, AsientoIngresoJobTareaEstado.OK));
            job.setTareasError((int) repository.countTareasByJobIdAndEstado(jobId, AsientoIngresoJobTareaEstado.ERROR));
            job.setTareasSinMovimientos((int) repository.countTareasByJobIdAndEstado(
                    jobId, AsientoIngresoJobTareaEstado.SIN_MOVIMIENTOS));
            job.setTareasPendientes((int) repository.countTareasByJobIdAndEstado(
                    jobId, AsientoIngresoJobTareaEstado.PENDIENTE));
            job.setFechaActual(tarea.getFechaContable());
            job.setTipoPagoActual(tarea.getTipoPagoId());
            job.setHeartbeat(ahora());
            repository.update(job);
        });
    }

    private static OffsetDateTime ahora() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

    private static String recortar(String texto, int largo) {
        if (texto == null) {
            return null;
        }
        return texto.length() <= largo ? texto : texto.substring(0, largo);
    }

}
