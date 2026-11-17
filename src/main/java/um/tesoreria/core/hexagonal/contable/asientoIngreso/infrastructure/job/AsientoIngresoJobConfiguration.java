package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Ejecutor dedicado del job masivo. El {@code @Scheduled} sólo reclama y despacha: si el job
 * corriera en el scheduler compartido demoraría a {@code AuditOutboxSender} y al resto de las
 * tareas programadas.
 */
@Configuration
public class AsientoIngresoJobConfiguration {

    @Bean(name = "asientoIngresoJobExecutor")
    public TaskExecutor asientoIngresoJobExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(1);
        executor.setThreadNamePrefix("asiento-ingreso-job-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }

}
