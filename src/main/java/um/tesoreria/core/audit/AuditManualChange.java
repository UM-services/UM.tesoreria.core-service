package um.tesoreria.core.audit;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Emite un evento {@code CHANGE} para una escritura que <b>no</b> pasa por el ciclo de vida
 * de JPA (JDBC, SQL nativo masivo, etc.).
 *
 * <p>Es la única forma admitida de cubrir una escritura fuera de JPA: el evento se escribe en
 * el mismo outbox y la misma transacción que el cambio, igual que los eventos que emiten los
 * listeners de Hibernate. Por eso {@code AuditCoverageGuard} exige que todo archivo de la
 * allowlist de escrituras directas invoque {@code auditManual.change(...)}.</p>
 *
 * <pre>{@code
 * auditManual.change(Change.scalar("CompraPedidoSecuencia", "compra_pedido_secuencia",
 *         ejercicioId + "/" + anio, "ultimoNumero", anterior, numero, Map.of("anio", anio)));
 * }</pre>
 *
 * <p>Respeta {@code tesoreria.audit.mode} ({@code OFF}/{@code LOG}/{@code SEND}) y
 * {@code change-events}. Si no hay transacción activa escribe el evento en el acto, con la
 * misma conexión.</p>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditManualChange {

    private final AuditProperties properties;
    private final AuditDiffBuilder diffBuilder;
    private final AuditBuffer buffer;
    private final AuditOutboxWriter writer;
    private final EntityManager entityManager;

    public void change(Change change) {
        if (properties.getMode() == AuditProperties.Mode.OFF || !properties.isChangeEvents()) {
            return;
        }

        List<String> changedFields = changedFields(change);
        if (changedFields.isEmpty()) {
            return;
        }

        AuditEventPayload payload = payload(change, changedFields);
        buffer.add(payload);
        AuditContext.incrementChanges();

        if (!buffer.markSynchronizationRegistered()) {
            return;
        }

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            SessionImplementor session = entityManager.unwrap(SessionImplementor.class);
            TransactionSynchronizationManager.registerSynchronization(
                    new AuditTransactionSynchronization(session, buffer, writer, properties));
        } else {
            // Sin transacción de Spring: se escribe en el acto, como hace AuditEntityListener.
            List<AuditEventPayload> pending = buffer.drain();
            if (properties.getMode() == AuditProperties.Mode.SEND) {
                writer.write(entityManager.unwrap(SessionImplementor.class), pending);
            } else {
                log.info("auditoría (modo LOG): {} evento(s) manual(es) sin transacción", pending.size());
            }
            buffer.clear();
        }
    }

    /**
     * Atajo para los correlativos anuales ({@code compra_*_secuencia}), que se reservan con
     * {@code LAST_INSERT_ID} por SQL directo y por eso no pasan por JPA.
     *
     * <p>{@code numero} es el valor ya reservado: {@code 1} implica alta de la fila del
     * ejercicio; cualquier otro valor implica incremento desde {@code numero - 1}.</p>
     */
    public void sequence(String entity, String table, Integer ejercicioId, Integer anio, Integer numero) {
        if (numero == null) {
            return;
        }
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("ejercicioId", ejercicioId);
        metadata.put("anio", anio);
        metadata.put("operacion", "reserva-correlativo");

        String entityId = ejercicioId + "/" + anio;
        if (numero <= 1) {
            change(Change.inserted(entity, table, entityId,
                    Map.of("ultimoNumero", numero), metadata));
        } else {
            change(Change.scalar(entity, table, entityId, "ultimoNumero",
                    numero - 1, numero, metadata));
        }
    }

    private List<String> changedFields(Change change) {        Set<String> fields = new LinkedHashSet<>();
        if (change.before() != null) {
            fields.addAll(change.before().keySet());
        }
        if (change.after() != null) {
            fields.addAll(change.after().keySet());
        }
        fields.removeIf(field -> "created".equals(field) || "updated".equals(field));
        return new ArrayList<>(fields);
    }

    private AuditEventPayload payload(Change change, List<String> changedFields) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("entityName", change.entity());
        metadata.put("table", change.table());
        metadata.put("source", "manual");
        if (change.metadata() != null) {
            metadata.putAll(change.metadata());
        }

        return AuditEventPayload.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(OffsetDateTime.now(ZoneOffset.UTC))
                .correlationId(AuditContext.correlationId())
                .service("tesoreria-core")
                .level("CHANGE")
                .operation(change.operation().name())
                .entity(change.entity())
                .entityId(change.entityId() == null ? null : change.entityId().toString())
                .actorType(AuditContext.actorType())
                .actorId(AuditContext.actorId())
                .actorLogin(AuditContext.actorLogin())
                .actorTrust(AuditContext.actorTrust())
                .clientIp(AuditContext.clientIp())
                .httpMethod(AuditContext.httpMethod())
                .endpoint(AuditContext.endpoint())
                .outcome("OK")
                .affectedRows(1)
                .changedFields(changedFields)
                .beforeData(mask(change.entity(), change.before()))
                .afterData(mask(change.entity(), change.after()))
                .metadata(metadata)
                .build();
    }

    private Map<String, Object> mask(String entity, Map<String, Object> data) {
        if (data == null) {
            return null;
        }
        Map<String, Object> masked = new LinkedHashMap<>();
        data.forEach((field, value) -> masked.put(field, diffBuilder.mask(entity, field, value)));
        return masked;
    }

    /** Un cambio de fila que no pasó por JPA. */
    public record Change(String entity,
                         String table,
                         String entityId,
                         AuditDiffBuilder.Operation operation,
                         Map<String, Object> before,
                         Map<String, Object> after,
                         Map<String, Object> metadata) {

        public static Change of(String entity, String table, Object entityId,
                                AuditDiffBuilder.Operation operation,
                                Map<String, Object> before, Map<String, Object> after,
                                Map<String, Object> metadata) {
            return new Change(entity, table, entityId == null ? null : entityId.toString(),
                    operation, before, after, metadata);
        }

        /** Atajo para un único campo escalar. */
        public static Change scalar(String entity, String table, Object entityId, String field,
                                    Object before, Object after, Map<String, Object> metadata) {
            return of(entity, table, entityId, AuditDiffBuilder.Operation.UPDATE,
                    before == null ? Map.of() : Map.of(field, before),
                    after == null ? Map.of() : Map.of(field, after),
                    metadata);
        }

        public static Change inserted(String entity, String table, Object entityId,
                                     Map<String, Object> after, Map<String, Object> metadata) {
            return of(entity, table, entityId, AuditDiffBuilder.Operation.INSERT, Map.of(), after, metadata);
        }

        public static Change deleted(String entity, String table, Object entityId,
                                    Map<String, Object> before, Map<String, Object> metadata) {
            return of(entity, table, entityId, AuditDiffBuilder.Operation.DELETE, before, Map.of(), metadata);
        }
    }
}
