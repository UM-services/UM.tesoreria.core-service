package um.tesoreria.core.hexagonal.mercadoPagoContext.application.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuración del circuito de Mercado Pago.
 *
 * <p>{@code preferenceOnDeuda=true} mantiene el comportamiento histórico: el GET de deuda
 * crea la preferencia si falta. {@code false} lo deja sólo lectura y la preferencia se pide
 * explícitamente por {@code POST /api/tesoreria/core/mercadopago/preference/{chequeraCuotaId}}.</p>
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "tesoreria.mercadopago")
public class MercadoPagoProperties {

    private boolean preferenceOnDeuda = true;

}
