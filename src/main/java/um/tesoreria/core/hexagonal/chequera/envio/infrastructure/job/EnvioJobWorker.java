package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.envio.application.service.EnvioChequerasService;
import um.tesoreria.core.hexagonal.chequera.envio.application.service.EnvioJobService;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJob;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobTarea;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.ResultadoEnvio;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Worker del envío masivo: reclama un job PENDIENTE y lo procesa tarea por tarea en un hilo propio.
 * Cada tarea es su propia transacción, así un error de negocio queda registrado y el job continúa.
 *
 * <p>El job cierra cuando no quedan tareas PENDIENTE; las ENCOLADA se resuelven después con el
 * acuse del sender ({@code notificacion-resultado}).</p>
 */
@Component
@Slf4j
public class EnvioJobWorker {

    private final EnvioJobService jobService;
    private final EnvioChequerasService orquestador;
    private final TaskExecutor executor;
    private final long staleMinutes;
    private final long retentionDays;
    private final long throttleMs;
    private final AtomicBoolean procesando = new AtomicBoolean(false);

    public EnvioJobWorker(EnvioJobService jobService,
                          EnvioChequerasService orquestador,
                          @Qualifier("chequerasEnvioJobExecutor") TaskExecutor executor,
                          @Value("${tesoreria.chequeras-envio.job.stale-minutes:15}") long staleMinutes,
                          @Value("${tesoreria.chequeras-envio.job.retention-days:30}") long retentionDays,
                          @Value("${tesoreria.chequeras-envio.job.throttle-ms:0}") long throttleMs) {
        this.jobService = jobService;
        this.orquestador = orquestador;
        this.executor = executor;
        this.staleMinutes = staleMinutes;
        this.retentionDays = retentionDays;
        this.throttleMs = throttleMs;
    }

    @Scheduled(fixedDelayString = "${tesoreria.chequeras-envio.job.delay-ms:5000}")
    public void despachar() {
        if (!procesando.compareAndSet(false, true)) {
            return;
        }
        try {
            Optional<EnvioJob> reclamado = jobService.reclamarSiguiente();
            if (reclamado.isEmpty()) {
                procesando.set(false);
                return;
            }
            Long jobId = reclamado.get().getJobId();
            executor.execute(() -> {
                try {
                    procesar(jobId);
                } catch (RuntimeException e) {
                    log.error("Envío masivo: job {} terminó con error de infraestructura", jobId, e);
                    jobService.marcarError(jobId, "Error de infraestructura: " + e.getMessage());
                } finally {
                    procesando.set(false);
                }
            });
        } catch (RuntimeException e) {
            procesando.set(false);
            log.error("Envío masivo: no se pudo reclamar el próximo job", e);
        }
    }

    @Scheduled(fixedDelayString = "${tesoreria.chequeras-envio.job.recovery-delay-ms:300000}")
    public void recuperarVencidos() {
        try {
            jobService.recuperarVencidos(staleMinutes);
        } catch (RuntimeException e) {
            log.error("Envío masivo: error al recuperar jobs vencidos", e);
        }
    }

    @Scheduled(cron = "0 55 4 * * *")
    public void purgarFinalizados() {
        try {
            jobService.purgar(retentionDays);
        } catch (RuntimeException e) {
            log.error("Envío masivo: error al purgar jobs finalizados", e);
        }
    }

    private void procesar(Long jobId) {
        EnvioJob job = jobService.buscar(jobId).orElse(null);
        if (job == null) {
            return;
        }
        for (EnvioJobTarea tarea : jobService.tareasPendientes(jobId)) {
            if (jobService.estaCancelado(jobId)) {
                jobService.marcarCancelado(jobId);
                return;
            }
            ResultadoEnvio resultado;
            try {
                resultado = orquestador.ejecutar(tarea, job);
            } catch (RuntimeException e) {
                log.error("Envío masivo: tarea de la chequera {} falló", tarea.getChequeraId(), e);
                resultado = ResultadoEnvio.error("ERROR_INTERNO", e.getMessage());
            }
            jobService.registrarTarea(jobId, tarea, resultado);
            esperarTurno();
        }
        if (jobService.estaCancelado(jobId)) {
            jobService.marcarCancelado(jobId);
            return;
        }
        jobService.completar(jobId);
    }

    /** Pausa entre filas para no agotar la cuota SMTP en campañas de miles. */
    private void esperarTurno() {
        if (throttleMs <= 0) {
            return;
        }
        try {
            Thread.sleep(throttleMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
