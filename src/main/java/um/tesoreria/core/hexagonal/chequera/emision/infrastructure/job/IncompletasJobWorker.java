package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.emision.application.service.CompletarChequerasIncompletasService;
import um.tesoreria.core.hexagonal.chequera.emision.application.service.IncompletasJobService;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasEtapa;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ResultadoIncompleta;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Worker del job de chequeras incompletas: reclama un job pendiente y lo procesa tarea por tarea
 * en un hilo propio. Cada tarea es su propia transacción, así un error de negocio queda registrado
 * y el job continúa, igual que el {@code For/Next} del legacy.
 */
@Component
@Slf4j
public class IncompletasJobWorker {

    private final IncompletasJobService jobService;
    private final CompletarChequerasIncompletasService orquestador;
    private final TaskExecutor executor;
    private final long staleMinutes;
    private final long retentionDays;
    private final AtomicBoolean procesando = new AtomicBoolean(false);

    public IncompletasJobWorker(IncompletasJobService jobService,
                                CompletarChequerasIncompletasService orquestador,
                                @Qualifier("chequerasIncompletasJobExecutor") TaskExecutor executor,
                                @Value("${tesoreria.chequeras-incompletas.job.stale-minutes:15}") long staleMinutes,
                                @Value("${tesoreria.chequeras-incompletas.job.retention-days:30}") long retentionDays) {
        this.jobService = jobService;
        this.orquestador = orquestador;
        this.executor = executor;
        this.staleMinutes = staleMinutes;
        this.retentionDays = retentionDays;
    }

    @Scheduled(fixedDelayString = "${tesoreria.chequeras-incompletas.job.delay-ms:5000}")
    public void despachar() {
        if (!procesando.compareAndSet(false, true)) {
            return;
        }
        try {
            Optional<IncompletasJob> reclamado = jobService.reclamarSiguiente();
            if (reclamado.isEmpty()) {
                procesando.set(false);
                return;
            }
            Long jobId = reclamado.get().getJobId();
            executor.execute(() -> {
                try {
                    procesar(jobId);
                } catch (RuntimeException e) {
                    log.error("Incompletas: job {} terminó con error de infraestructura", jobId, e);
                    jobService.marcarError(jobId, "Error de infraestructura: " + e.getMessage());
                } finally {
                    procesando.set(false);
                }
            });
        } catch (RuntimeException e) {
            procesando.set(false);
            log.error("Incompletas: no se pudo reclamar el próximo job", e);
        }
    }

    @Scheduled(fixedDelayString = "${tesoreria.chequeras-incompletas.job.recovery-delay-ms:300000}")
    public void recuperarVencidos() {
        try {
            jobService.recuperarVencidos(staleMinutes);
        } catch (RuntimeException e) {
            log.error("Incompletas: error al recuperar jobs vencidos", e);
        }
    }

    @Scheduled(cron = "0 50 4 * * *")
    public void purgarFinalizados() {
        try {
            jobService.purgar(retentionDays);
        } catch (RuntimeException e) {
            log.error("Incompletas: error al purgar jobs finalizados", e);
        }
    }

    private void procesar(Long jobId) {
        IncompletasJob job = jobService.buscar(jobId).orElse(null);
        if (job == null) {
            return;
        }
        for (IncompletasJobTarea tarea : jobService.tareasPendientes(jobId)) {
            if (jobService.estaCancelado(jobId)) {
                jobService.marcarCancelado(jobId);
                return;
            }
            ResultadoIncompleta resultado;
            try {
                resultado = job.getEtapa() == IncompletasEtapa.BORRADORES
                        ? orquestador.prepararBorrador(tarea, job.getUsuarioLogin())
                        : orquestador.emitir(tarea, job.getUsuarioLogin());
            } catch (RuntimeException e) {
                log.error("Incompletas: tarea de la chequera {} falló", tarea.getChequeraId(), e);
                resultado = ResultadoIncompleta.error("ERROR_INTERNO", e.getMessage());
            }
            jobService.registrarTarea(jobId, tarea, resultado);
        }
        if (jobService.estaCancelado(jobId)) {
            jobService.marcarCancelado(jobId);
            return;
        }
        jobService.completar(jobId);
    }

}
