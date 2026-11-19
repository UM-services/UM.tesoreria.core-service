package um.tesoreria.core.hexagonal.chequera.emision.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasTareaEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ResultadoIncompleta;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.IncompletasJobRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Operaciones del job de chequeras incompletas que usa el worker: reclamar, registrar el
 * resultado de cada tarea y cerrar. Cada actualización es una transacción corta.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IncompletasJobService {

    private final IncompletasJobRepository repository;
    private final TransactionOperations transaction;

    public Optional<IncompletasJob> reclamarSiguiente() {
        return transaction.execute(status -> repository.claimNextPending().map(job -> {
            OffsetDateTime ahora = ahora();
            job.setEstado(IncompletasJobEstado.EN_PROCESO);
            job.setHeartbeat(ahora);
            if (job.getIniciado() == null) {
                job.setIniciado(ahora);
            }
            return repository.update(job);
        }));
    }

    public List<IncompletasJobTarea> tareasPendientes(Long jobId) {
        return repository.findPendingTareasByJobId(jobId);
    }

    public Optional<IncompletasJob> buscar(Long jobId) {
        return repository.findByJobId(jobId);
    }

    public boolean estaCancelado(Long jobId) {
        return repository.findByJobId(jobId)
                .map(job -> job.getEstado() == IncompletasJobEstado.CANCELADO
                        || job.getEstado() == IncompletasJobEstado.ERROR)
                .orElse(true);
    }

    public void registrarTarea(Long jobId, IncompletasJobTarea tarea, ResultadoIncompleta resultado) {
        transaction.executeWithoutResult(status -> {
            tarea.setEstado(resultado.getEstado());
            tarea.setCodigo(recortar(resultado.getCodigo(), 60));
            tarea.setMensaje(recortar(resultado.getMensaje(), 500));
            if (resultado.getBorradorId() != null) {
                tarea.setBorradorId(resultado.getBorradorId());
            }
            tarea.setFinalizado(ahora());
            repository.updateTarea(tarea);
            actualizarJob(jobId, tarea);
        });
    }

    public void completar(Long jobId) {
        transaction.executeWithoutResult(status -> repository.findWithLockByJobId(jobId).ifPresent(job -> {
            job.setEstado(IncompletasJobEstado.COMPLETADO);
            job.setFinalizado(ahora());
            job.setHeartbeat(ahora());
            job.setTareasPendientes((int) repository.countTareasByJobIdAndEstado(
                    jobId, IncompletasTareaEstado.PENDIENTE));
            repository.update(job);
        }));
    }

    public void marcarCancelado(Long jobId) {
        transaction.executeWithoutResult(status -> repository.findWithLockByJobId(jobId).ifPresent(job -> {
            job.setEstado(IncompletasJobEstado.CANCELADO);
            job.setFinalizado(ahora());
            job.setHeartbeat(ahora());
            repository.update(job);
        }));
    }

    public void marcarError(Long jobId, String mensaje) {
        transaction.executeWithoutResult(status -> repository.findWithLockByJobId(jobId).ifPresent(job -> {
            job.setEstado(IncompletasJobEstado.ERROR);
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
            log.warn("Incompletas: {} job(s) reencolados por heartbeat vencido", total);
        }
        return total;
    }

    public int purgar(long dias) {
        Integer purgados = transaction.execute(status ->
                repository.deleteFinalizedBefore(ahora().minusDays(dias)));
        int total = Objects.requireNonNullElse(purgados, 0);
        if (total > 0) {
            log.info("Incompletas: {} job(s) finalizados eliminados", total);
        }
        return total;
    }

    private void actualizarJob(Long jobId, IncompletasJobTarea tarea) {
        repository.findWithLockByJobId(jobId).ifPresent(job -> {
            job.setTareasOk((int) repository.countTareasByJobIdAndEstadoIn(jobId, ESTADOS_OK));
            job.setTareasOmitidas((int) repository.countTareasByJobIdAndEstado(
                    jobId, IncompletasTareaEstado.OMITIDA));
            job.setTareasError((int) repository.countTareasByJobIdAndEstado(
                    jobId, IncompletasTareaEstado.ERROR));
            job.setTareasPendientes((int) repository.countTareasByJobIdAndEstado(
                    jobId, IncompletasTareaEstado.PENDIENTE));
            job.setUltimaChequeraId(tarea.getChequeraId());
            job.setHeartbeat(ahora());
            repository.update(job);
        });
    }

    private static final List<IncompletasTareaEstado> ESTADOS_OK = List.of(
            IncompletasTareaEstado.BORRADOR_CREADO,
            IncompletasTareaEstado.BORRADOR_REUTILIZADO,
            IncompletasTareaEstado.EMITIDA);

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
