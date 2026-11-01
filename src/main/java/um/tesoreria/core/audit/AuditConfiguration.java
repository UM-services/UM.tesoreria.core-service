package um.tesoreria.core.audit;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Habilita las propiedades de auditoría y registra el filtro HTTP.
 *
 * <p>El filtro se declara como bean y no como {@code @Component} a propósito: así los
 * {@code @WebMvcTest} no lo instancian sin {@link AuditProperties}. El
 * {@code @EnableScheduling} ya está en {@code TesoreriaConfiguration}, así que el sender corre
 * sin configuración extra.</p>
 */
@Configuration
@EnableConfigurationProperties(AuditProperties.class)
public class AuditConfiguration {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE + 10)
    AuditRequestFilter auditRequestFilter(AuditProperties properties, AuditOutboxWriter writer) {
        return new AuditRequestFilter(properties, writer);
    }
}
