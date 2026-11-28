package um.tesoreria.core.hexagonal.chequera.emision.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionTareaEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ResultadoGeneracionFila;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.GeneracionJobRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Operaciones del job de generación masiva que usa el worker: reclamar, registrar el resultado de
 * cada tarea y cerrar. Cada actualización es una transacción corta.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GeneracionJobService {

    private static final List<GeneracionTareaEstado> ESTADOS_OK = List.of(
            GeneracionTareaEstado.BORRADOR_CREADO,
            GeneracionTareaEstado.BORRADOR_REUTILIZADO,
            GeneracionTareaEstado.EMITIDA);

    private final GeneracionJobRepository repository;
    private final TransactionOperations transaction;

    public Optional<GeneracionJob> reclamarSiguiente() {
        return transaction.execute(status -> repository.claimNextPending().map(job -> {
            OffsetDateTime ahora = ahora();
            job.setEstado(GeneracionJobEstado.EN_PROCESO);
            job.setHeartbeat(ahora);
            if (job.getIniciado() == null) {
                job.setIniciado(ahora);
            }
            return repository.update(job);
        }));
    }

    public List<GeneracionJobTarea> tareasPendientes(Long jobId) {
        return repository.findPendingTareasByJobId(jobId);
    }

    public Optional<GeneracionJob> buscar(Long jobId) {
        return repository.findByJobId(jobId);
    }

    public boolean estaCancelado(Long jobId) {
        return repository.findByJobId(jobId)
                .map(job -> job.getEstado() == GeneracionJobEstado.CANCELADO
                        || job.getEstado() == GeneracionJobEstado.ERROR)
                .orElse(true);
    }

    public void registrarTarea(Long jobId, GeneracionJobTarea tarea, ResultadoGeneracionFila resultado) {
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
            job.setEstado(GeneracionJobEstado.COMPLETADO);
            job.setFinalizado(ahora());
            job.setHeartbeat(ahora());
            job.setTareasPendientes((int) repository.countTareasByJobIdAndEstado(
                    jobId, GeneracionTareaEstado.PENDIENTE));
            repository.update(job);
        }));
    }

    public void marcarCancelado(Long jobId) {
        transaction.executeWithoutResult(status -> repository.findWithLockByJobId(jobId).ifPresent(job -> {
            job.setEstado(GeneracionJobEstado.CANCELADO);
            job.setFinalizado(ahora());
            job.setHeartbeat(ahora());
            repository.update(job);
        }));
    }

    public void marcarError(Long jobId, String mensaje) {
        transaction.executeWithoutResult(status -> repository.findWithLockByJobId(jobId).ifPresent(job -> {
            job.setEstado(GeneracionJobEstado.ERROR);
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
            log.warn("Generación: {} job(s) reencolados por heartbeat vencido", total);
        }
        return total;
    }

    public int purgar(long dias) {
        Integer purgados = transaction.execute(status ->
                repository.deleteFinalizedBefore(ahora().minusDays(dias)));
        int total = Objects.requireNonNullElse(purgados, 0);
        if (total > 0) {
            log.info("Generación: {} job(s) finalizados eliminados", total);
        }
        return total;
    }

    private void actualizarJob(Long jobId, GeneracionJobTarea tarea) {
        repository.findWithLockByJobId(jobId).ifPresent(job -> {
            job.setTareasOk((int) repository.countTareasByJobIdAndEstadoIn(jobId, ESTADOS_OK));
            job.setTareasOmitidas((int) repository.countTareasByJobIdAndEstado(
                    jobId, GeneracionTareaEstado.OMITIDA));
            job.setTareasError((int) repository.countTareasByJobIdAndEstado(
                    jobId, GeneracionTareaEstado.ERROR));
            job.setTareasPendientes((int) repository.countTareasByJobIdAndEstado(
                    jobId, GeneracionTareaEstado.PENDIENTE));
            job.setUltimaChequeraId(tarea.getChequeraId());
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
