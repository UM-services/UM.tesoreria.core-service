package um.tesoreria.core.hexagonal.chequera.envio.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJob;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobEstado;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobTarea;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioTareaEstado;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.ResultadoEnvio;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.EnvioJobRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Reclamar, registrar el resultado de cada tarea y cerrar. Cada paso es una transacción corta. */
@Service
@RequiredArgsConstructor
@Slf4j
public class EnvioJobService {

    private static final List<EnvioTareaEstado> ESTADOS_OK = List.of(
            EnvioTareaEstado.ENVIADA,
            EnvioTareaEstado.RETENIDA,
            EnvioTareaEstado.ENCOLADA,
            EnvioTareaEstado.CORREO_CAPTURADO,
            EnvioTareaEstado.DEBITO_EXTENDIDO);

    private static final List<EnvioTareaEstado> ESTADOS_OMITIDA = List.of(
            EnvioTareaEstado.OMITIDA,
            EnvioTareaEstado.SIN_CORREOS);

    private static final List<EnvioTareaEstado> ESTADOS_ERROR = List.of(
            EnvioTareaEstado.ERROR,
            EnvioTareaEstado.ERROR_SMTP);

    private final EnvioJobRepository repository;
    private final TransactionOperations transaction;

    public Optional<EnvioJob> reclamarSiguiente() {
        return transaction.execute(status -> repository.claimNextPending().map(job -> {
            OffsetDateTime ahora = ahora();
            job.setEstado(EnvioJobEstado.EN_PROCESO);
            job.setHeartbeat(ahora);
            if (job.getIniciado() == null) {
                job.setIniciado(ahora);
            }
            return repository.update(job);
        }));
    }

    public List<EnvioJobTarea> tareasPendientes(Long jobId) {
        return repository.findPendingTareasByJobId(jobId);
    }

    public Optional<EnvioJob> buscar(Long jobId) {
        return repository.findByJobId(jobId);
    }

    public boolean estaCancelado(Long jobId) {
        return repository.findByJobId(jobId)
                .map(job -> job.getEstado() == EnvioJobEstado.CANCELADO || job.getEstado() == EnvioJobEstado.ERROR)
                .orElse(true);
    }

    public void registrarTarea(Long jobId, EnvioJobTarea tarea, ResultadoEnvio resultado) {
        transaction.executeWithoutResult(status -> {
            tarea.setEstado(resultado.getEstado());
            tarea.setCodigo(recortar(resultado.getCodigo(), 60));
            tarea.setMensaje(recortar(resultado.getMensaje(), 500));
            tarea.setDetalle(recortar(resultado.getDetalle(), 600));
            tarea.setRetenida(resultado.getRetenida());
            tarea.setEnviado(resultado.getEnviado());
            tarea.setFinalizado(ahora());
            repository.updateTarea(tarea);
            actualizarJob(jobId, tarea);
        });
    }

    public void completar(Long jobId) {
        transaction.executeWithoutResult(status -> repository.findWithLockByJobId(jobId).ifPresent(job -> {
            job.setEstado(EnvioJobEstado.COMPLETADO);
            job.setFinalizado(ahora());
            job.setHeartbeat(ahora());
            job.setTareasPendientes((int) repository.countTareasByJobIdAndEstado(jobId, EnvioTareaEstado.PENDIENTE));
            job.setTareasOk((int) repository.countTareasByJobIdAndEstadoIn(jobId, ESTADOS_OK));
            job.setTareasOmitidas((int) repository.countTareasByJobIdAndEstadoIn(jobId, ESTADOS_OMITIDA));
            job.setTareasError((int) repository.countTareasByJobIdAndEstadoIn(jobId, ESTADOS_ERROR));
            repository.update(job);
        }));
    }

    /** Acuse del sender: cierra la tarea ENCOLADA de esa chequera (ENVIADA o ERROR_SMTP). */
    public boolean registrarAcuse(Long chequeraId, boolean enviado, String error) {
        return repository.registrarAcuse(chequeraId, enviado, error).isPresent();
    }

    public void marcarCancelado(Long jobId) {
        transaction.executeWithoutResult(status -> repository.findWithLockByJobId(jobId).ifPresent(job -> {
            job.setEstado(EnvioJobEstado.CANCELADO);
            job.setFinalizado(ahora());
            job.setHeartbeat(ahora());
            repository.update(job);
        }));
    }

    public void marcarError(Long jobId, String mensaje) {
        transaction.executeWithoutResult(status -> repository.findWithLockByJobId(jobId).ifPresent(job -> {
            job.setEstado(EnvioJobEstado.ERROR);
            job.setMensaje(recortar(mensaje, 500));
            job.setFinalizado(ahora());
            job.setHeartbeat(ahora());
            repository.update(job);
        }));
    }

    public int recuperarVencidos(long minutos) {
        Integer recuperados = transaction.execute(status -> repository.requeueStale(ahora().minusMinutes(minutos)));
        int total = Objects.requireNonNullElse(recuperados, 0);
        if (total > 0) {
            log.warn("Envío masivo: {} job(s) reencolados por heartbeat vencido", total);
        }
        return total;
    }

    public int purgar(long dias) {
        Integer purgados = transaction.execute(status -> repository.deleteFinalizedBefore(ahora().minusDays(dias)));
        int total = Objects.requireNonNullElse(purgados, 0);
        if (total > 0) {
            log.info("Envío masivo: {} job(s) finalizados eliminados", total);
        }
        return total;
    }

    private void actualizarJob(Long jobId, EnvioJobTarea tarea) {
        repository.findWithLockByJobId(jobId).ifPresent(job -> {
            job.setTareasOk((int) repository.countTareasByJobIdAndEstadoIn(jobId, ESTADOS_OK));
            job.setTareasOmitidas((int) repository.countTareasByJobIdAndEstadoIn(jobId, ESTADOS_OMITIDA));
            job.setTareasError((int) repository.countTareasByJobIdAndEstadoIn(jobId, ESTADOS_ERROR));
            job.setTareasPendientes((int) repository.countTareasByJobIdAndEstado(jobId, EnvioTareaEstado.PENDIENTE));
            job.setUltimaChequeraId(tarea.getChequeraId());
            job.setHeartbeat(ahora());
            repository.update(job);
        });
    }

    private static OffsetDateTime ahora() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

    private static String recortar(String texto, int maximo) {
        if (texto == null) {
            return null;
        }
        return texto.length() <= maximo ? texto : texto.substring(0, maximo);
    }
}
