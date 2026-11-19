package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Ejecutor dedicado del job de chequeras incompletas: el {@code @Scheduled} sólo reclama y
 * despacha, para no demorar al resto de las tareas programadas.
 */
@Configuration
public class IncompletasJobConfiguration {

    @Bean(name = "chequerasIncompletasJobExecutor")
    public TaskExecutor chequerasIncompletasJobExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(1);
        executor.setThreadNamePrefix("chequeras-incompletas-job-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }

}
