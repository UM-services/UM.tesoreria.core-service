package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.exception.AsientoIngresoException;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service.AsientoIngresoJobService;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJob;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobTarea;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.in.RegenerarAsientoIngresoUseCase;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Worker del masivo: reclama un job pendiente y lo procesa tarea por tarea en un hilo propio. Cada
 * tarea es su propia transacción, así un error de negocio (por ejemplo una cuenta de pago faltante)
 * queda registrado y el job continúa, como el bucle del legacy.
 */
@Component
@Slf4j
public class AsientoIngresoJobWorker {

    private final AsientoIngresoJobService jobService;
    private final RegenerarAsientoIngresoUseCase regenerarUseCase;
    private final TaskExecutor executor;
    private final long staleMinutes;
    private final long retentionDays;
    private final AtomicBoolean procesando = new AtomicBoolean(false);

    public AsientoIngresoJobWorker(AsientoIngresoJobService jobService,
                                   RegenerarAsientoIngresoUseCase regenerarUseCase,
                                   @Qualifier("asientoIngresoJobExecutor") TaskExecutor executor,
                                   @Value("${tesoreria.asiento-ingreso.job.stale-minutes:15}") long staleMinutes,
                                   @Value("${tesoreria.asiento-ingreso.job.retention-days:30}") long retentionDays) {
        this.jobService = jobService;
        this.regenerarUseCase = regenerarUseCase;
        this.executor = executor;
        this.staleMinutes = staleMinutes;
        this.retentionDays = retentionDays;
    }

    @Scheduled(fixedDelayString = "${tesoreria.asiento-ingreso.job.delay-ms:5000}")
    public void despachar() {
        if (!procesando.compareAndSet(false, true)) {
            return;
        }
        try {
            Optional<AsientoIngresoJob> reclamado = jobService.reclamarSiguiente();
            if (reclamado.isEmpty()) {
                procesando.set(false);
                return;
            }
            Long jobId = reclamado.get().getJobId();
            executor.execute(() -> {
                try {
                    procesar(jobId);
                } catch (RuntimeException e) {
                    log.error("Asiento ingreso: job {} terminó con error de infraestructura", jobId, e);
                    jobService.marcarError(jobId, "Error de infraestructura: " + e.getMessage());
                } finally {
                    procesando.set(false);
                }
            });
        } catch (RuntimeException e) {
            procesando.set(false);
            log.error("Asiento ingreso: no se pudo reclamar el próximo job", e);
        }
    }

    @Scheduled(fixedDelayString = "${tesoreria.asiento-ingreso.job.recovery-delay-ms:300000}")
    public void recuperarVencidos() {
        try {
            jobService.recuperarVencidos(staleMinutes);
        } catch (RuntimeException e) {
            log.error("Asiento ingreso: error al recuperar jobs vencidos", e);
        }
    }

    @Scheduled(cron = "0 40 4 * * *")
    public void purgarFinalizados() {
        try {
            jobService.purgar(retentionDays);
        } catch (RuntimeException e) {
            log.error("Asiento ingreso: error al purgar jobs finalizados", e);
        }
    }

    private void procesar(Long jobId) {
        for (AsientoIngresoJobTarea tarea : jobService.tareasPendientes(jobId)) {
            if (jobService.estaCancelado(jobId)) {
                jobService.marcarCancelado(jobId);
                return;
            }
            try {
                var resultado = regenerarUseCase.regenerar(tarea.getFechaContable(), tarea.getTipoPagoId());
                jobService.registrarTareaOk(jobId, tarea, resultado);
            } catch (AsientoIngresoException e) {
                jobService.registrarTareaError(jobId, tarea, e.getCodigo().name(), e.getMessage());
            } catch (RuntimeException e) {
                log.error("Asiento ingreso: tarea {} tipo {} falló", tarea.getFechaContable(),
                        tarea.getTipoPagoId(), e);
                jobService.registrarTareaError(jobId, tarea, "ERROR_INTERNO", e.getMessage());
            }
        }
        if (jobService.estaCancelado(jobId)) {
            jobService.marcarCancelado(jobId);
            return;
        }
        jobService.completar(jobId);
    }

}
