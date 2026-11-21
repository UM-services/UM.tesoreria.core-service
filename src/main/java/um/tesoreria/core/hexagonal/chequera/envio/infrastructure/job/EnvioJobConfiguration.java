package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.job;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Ejecutor dedicado del envío masivo: el {@code @Scheduled} sólo reclama y despacha. Un solo hilo
 * porque un job procesa una fila a la vez (paridad con el {@code For/Next} del legacy).
 */
@Configuration
public class EnvioJobConfiguration {

    @Bean(name = "chequerasEnvioJobExecutor")
    public TaskExecutor chequerasEnvioJobExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(1);
        executor.setThreadNamePrefix("chequeras-envio-job-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return executor;
    }
}
