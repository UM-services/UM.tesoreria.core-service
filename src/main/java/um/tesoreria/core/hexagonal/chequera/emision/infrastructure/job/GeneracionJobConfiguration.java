package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Ejecutor dedicado del job de generación masiva: el {@code @Scheduled} sólo reclama y despacha,
 * para no demorar al resto de las tareas programadas.
 */
@Configuration
public class GeneracionJobConfiguration {

    @Bean(name = "chequerasGeneracionJobExecutor")
    public TaskExecutor chequerasGeneracionJobExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(1);
        executor.setThreadNamePrefix("chequeras-generacion-job-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }

}
