package um.tesoreria.core.client.haberes.core;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Configuración por cliente Feign para las llamadas a haberes-core.
 *
 * <p>No se anota con {@code @Configuration} a propósito: se aplica sólo a los
 * clientes que la declaran (`CursoClient`, `CargoTipoClient`) y no propaga el
 * token a los demás servicios de Tesorería.
 */
public class HaberesServiceTokenFeignConfig {

    @Value("${HABERES_SERVICE_TOKEN:}")
    private String serviceToken;

    @Bean
    public RequestInterceptor haberesServiceTokenInterceptor() {
        return template -> {
            if (serviceToken == null || serviceToken.isBlank()) {
                return;
            }
            if (template.headers().containsKey("Authorization")) {
                return;
            }
            template.header("Authorization", "Bearer " + serviceToken.trim());
        };
    }
}
