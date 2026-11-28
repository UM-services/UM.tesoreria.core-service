package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.emision.application.service.GeneracionBorradoresService;
import um.tesoreria.core.hexagonal.chequera.emision.application.service.GeneracionJobService;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionEtapa;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ResultadoGeneracionFila;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Worker del job de generación masiva: reclama un job pendiente y lo procesa tarea por tarea en un
 * hilo propio. Cada tarea es su propia transacción, así un error de negocio queda registrado y el
 * job continúa.
 */
@Component
@Slf4j
public class GeneracionJobWorker {

    private final GeneracionJobService jobService;
    private final GeneracionBorradoresService orquestador;
    private final TaskExecutor executor;
    private final long staleMinutes;
    private final long retentionDays;
    private final long throttleMs;
    private final AtomicBoolean procesando = new AtomicBoolean(false);

    public GeneracionJobWorker(GeneracionJobService jobService,
                               GeneracionBorradoresService orquestador,
                               @Qualifier("chequerasGeneracionJobExecutor") TaskExecutor executor,
                               @Value("${tesoreria.chequeras-generacion.job.stale-minutes:15}") long staleMinutes,
                               @Value("${tesoreria.chequeras-generacion.job.retention-days:30}") long retentionDays,
                               @Value("${tesoreria.chequeras-generacion.job.throttle-ms:0}") long throttleMs) {
        this.jobService = jobService;
        this.orquestador = orquestador;
        this.executor = executor;
        this.staleMinutes = staleMinutes;
        this.retentionDays = retentionDays;
        this.throttleMs = throttleMs;
    }

    @Scheduled(fixedDelayString = "${tesoreria.chequeras-generacion.job.delay-ms:5000}")
    public void despachar() {
        if (!procesando.compareAndSet(false, true)) {
            return;
        }
        try {
            Optional<GeneracionJob> reclamado = jobService.reclamarSiguiente();
            if (reclamado.isEmpty()) {
                procesando.set(false);
                return;
            }
            Long jobId = reclamado.get().getJobId();
            executor.execute(() -> {
                try {
                    procesar(jobId);
                } catch (RuntimeException e) {
                    log.error("Generación: job {} terminó con error de infraestructura", jobId, e);
                    jobService.marcarError(jobId, "Error de infraestructura: " + e.getMessage());
                } finally {
                    procesando.set(false);
                }
            });
        } catch (RuntimeException e) {
            procesando.set(false);
            log.error("Generación: no se pudo reclamar el próximo job", e);
        }
    }

    @Scheduled(fixedDelayString = "${tesoreria.chequeras-generacion.job.recovery-delay-ms:300000}")
    public void recuperarVencidos() {
        try {
            jobService.recuperarVencidos(staleMinutes);
        } catch (RuntimeException e) {
            log.error("Generación: error al recuperar jobs vencidos", e);
        }
    }

    @Scheduled(cron = "0 45 4 * * *")
    public void purgarFinalizados() {
        try {
            jobService.purgar(retentionDays);
        } catch (RuntimeException e) {
            log.error("Generación: error al purgar jobs finalizados", e);
        }
    }

    private void procesar(Long jobId) {
        GeneracionJob job = jobService.buscar(jobId).orElse(null);
        if (job == null) {
            return;
        }
        for (GeneracionJobTarea tarea : jobService.tareasPendientes(jobId)) {
            if (jobService.estaCancelado(jobId)) {
                jobService.marcarCancelado(jobId);
                return;
            }
            ResultadoGeneracionFila resultado;
            try {
                resultado = job.getEtapa() == GeneracionEtapa.BORRADORES
                        ? orquestador.prepararBorrador(tarea, job.getUsuarioLogin())
                        : orquestador.emitir(tarea, job);
            } catch (RuntimeException e) {
                log.error("Generación: tarea de la chequera {} falló", tarea.getChequeraId(), e);
                resultado = ResultadoGeneracionFila.error("ERROR_INTERNO", e.getMessage());
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
