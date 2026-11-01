package um.tesoreria.core.audit;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@ConfigurationProperties(prefix = "tesoreria.audit")
public class AuditProperties {

    public enum Mode {
        /** No captura nada. */
        OFF,
        /** Captura y mide, pero no escribe en la base ni envía. */
        LOG,
        /** Escribe en audit_outbox y envía al servicio de auditoría. */
        SEND
    }

    private Mode mode = Mode.SEND;

    private boolean requestEvents = true;

    private boolean changeEvents = true;

    private String url = "http://localhost:8211";

    private String apiKey = "";

    private int batchSize = 200;

    private long senderDelayMs = 2000;

    private long senderInitialDelayMs = 5000;

    private int maxAttempts = 10;

    private int maxPayloadChars = 32768;

    /**
     * Campos que se enmascaran antes de serializar. Acepta dos formas:
     *
     * <ul>
     *   <li>{@code campo} — se enmascara en cualquier entidad (ej. {@code password}).</li>
     *   <li>{@code Entidad.campo} — sólo en esa entidad; el nombre se compara por nombre
     *       simple, sin distinguir mayúsculas y sin el sufijo {@code Entity}
     *       (ej. {@code Usuario.clave} alcanza a {@code UsuarioEntity}).</li>
     * </ul>
     *
     * <p>{@code clave} y {@code cuenta} no están en el default global a propósito: en
     * {@code PermisoEntity} la primera es la clave funcional del permiso, y en
     * {@code CuentaMensual}/{@code FacultadPagoCuenta}/{@code ChequeraPagoAsiento} la segunda
     * es un número de cuenta contable (dato de negocio), no una cuenta bancaria. Si una
     * entidad las usa como dato sensible, se agregan acotadas
     * ({@code Usuario.clave}, {@code Bancaria.cuenta}).</p>
     */
    private List<String> maskFields = new ArrayList<>(
            List.of("password", "token", "cbu", "documento", "tarjeta"));

    /**
     * Entidades que nunca se auditan: logs técnicos, tablas de infraestructura o historia
     * que ya es auditoría. Vacío por defecto: en Tesorería no hay todavía ninguna.
     */
    private Set<String> excludedEntities = new HashSet<>();

    /**
     * Kill switch del resumen masivo: con {@code false} los scopes son inertes y todo se
     * audita fila por fila, sin necesidad de deploy.
     */
    private boolean bulkEnabled = true;

    /**
     * Cantidad mínima de filas para resumir una generación como {@code SUMMARY}. Por debajo,
     * se audita fila por fila (una recalculación de una cuota no debe perderse en un resumen).
     * Las bajas nunca se resumen: son irreversibles y necesitan before-image por fila.
     */
    private int bulkMinRows = 20;

    /**
     * Entidades elegibles para resumirse dentro de un {@link AuditBulkScope}. Fuera del
     * scope siguen emitiendo su evento {@code CHANGE} normal: la configuración no silencia
     * nada por sí sola.
     */
    private Set<String> summaryEntities = new HashSet<>(Set.of(
            "ChequeraCuotaEntity", "ChequeraSerieEntity", "ChequeraTotalEntity"));
}
