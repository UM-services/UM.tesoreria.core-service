package um.tesoreria.core.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuración del registro de impresiones de chequeras.
 *
 * <p>{@code maxHistory} acota cuántos snapshots se conservan por chequera. El sistema sólo
 * consume el último ({@code findLastByUnique}); los anteriores se purgan al insertar uno nuevo.
 * {@code 0} desactiva la purga.</p>
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "tesoreria.chequera.impresion")
public class ChequeraImpresionProperties {

    private int maxHistory = 10;

}
