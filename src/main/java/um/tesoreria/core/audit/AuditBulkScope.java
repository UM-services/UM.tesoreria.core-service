package um.tesoreria.core.audit;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SharedSessionContractImplementor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Scope de una operación masiva: en lugar de un evento por fila, cuenta los cambios y emite
 * un evento {@code SUMMARY/BULK} por entidad y operación al cerrar.
 *
 * <p>Se abre con {@link AuditBulkService#begin(BulkContext)} y se cierra al terminar la
 * operación (try-with-resources). Al cerrar se hace un {@code flush} <b>antes</b> de salir del
 * scope: así los {@code PostInsert}/{@code PostDelete} pendientes se cuentan dentro del scope
 * y no se emiten además como eventos por fila.</p>
 *
 * <p>Sólo se resumen las entidades de {@code tesoreria.audit.summary-entities}. El resto de las
 * escrituras de la misma transacción sigue generando su evento {@code CHANGE} normal.</p>
 */
@Slf4j
public final class AuditBulkScope implements AutoCloseable {

    private static final ThreadLocal<AuditBulkScope> CURRENT = new ThreadLocal<>();

    private final boolean active;
    private final BulkContext context;
    private final Set<String> summaryEntities;
    private final AuditBuffer buffer;
    private final SharedSessionContractImplementor session;
    private final Map<String, Counter> counters = new LinkedHashMap<>();
    private boolean closed;

    private AuditBulkScope(boolean active, BulkContext context, Set<String> summaryEntities,
                           AuditBuffer buffer, SharedSessionContractImplementor session) {
        this.active = active;
        this.context = context;
        this.summaryEntities = summaryEntities;
        this.buffer = buffer;
        this.session = session;
    }

    static AuditBulkScope open(BulkContext context, Set<String> summaryEntities,
                               AuditBuffer buffer, SharedSessionContractImplementor session) {
        AuditBulkScope scope = new AuditBulkScope(true, context, summaryEntities, buffer, session);
        CURRENT.set(scope);
        return scope;
    }

    /** Scope inerte cuando la captura está apagada: no suprime ni emite nada. */
    static AuditBulkScope disabled() {
        return new AuditBulkScope(false, null, Set.of(), null, null);
    }

    static AuditBulkScope current() {
        return CURRENT.get();
    }

    void count(String entity, String table, AuditDiffBuilder.Operation operation) {
        if (!active) {
            return;
        }
        counters.computeIfAbsent(entity, key -> new Counter(table)).increment(operation);
    }

    @Override
    public void close() {
        if (!active || closed) {
            return;
        }
        closed = true;
        try {
            if (session != null && session.isOpen()) {
                session.flush();
            }
        } finally {
            CURRENT.remove();
        }
        emit();
    }

    private void emit() {
        counters.forEach(this::emitEntity);
        counters.clear();
    }

    private void emitEntity(String entity, Counter counter) {
        int affected = counter.total();
        if (affected == 0) {
            return;
        }
        Map<String, Object> counts = new LinkedHashMap<>();
        counter.byOperation.forEach((operation, count) -> counts.put(operation.name(), count));
        buffer.add(payload(entity, counter.table, affected, counts));
    }

    private AuditEventPayload payload(String entity, String table, int affected,
                                      Map<String, Object> counts) {
        String correlation = AuditContext.correlationId() != null
                ? AuditContext.correlationId()
                : context.bulkId();

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("entityName", entity);
        metadata.put("table", table);
        metadata.put("counts", counts);
        metadata.put("label", context.label());
        metadata.put("bulkId", context.bulkId());
        if (context.anho() != null && context.mes() != null) {
            metadata.put("period", Map.of("anho", context.anho(), "mes", context.mes()));
        }
        if (context.legajoId() != null) {
            metadata.put("filter", Map.of("legajoId", context.legajoId()));
        }
        if (context.chunkSize() > 0) {
            metadata.put("chunk", Map.of("index", context.chunkIndex(), "size", context.chunkSize()));
        }

        return AuditEventPayload.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(OffsetDateTime.now(ZoneOffset.UTC))
                .correlationId(correlation)
                .service("tesoreria-core")
                .level("SUMMARY")
                .operation("BULK")
                .entity(entity)
                .actorType(AuditContext.actorType())
                .actorId(AuditContext.actorId())
                .actorLogin(AuditContext.actorLogin())
                .actorTrust(AuditContext.actorTrust())
                .clientIp(AuditContext.clientIp())
                .httpMethod(AuditContext.httpMethod())
                .endpoint(AuditContext.endpoint())
                .outcome("OK")
                .affectedRows(affected)
                .metadata(metadata)
                .build();
    }

    private static final class Counter {

        private final String table;
        private final Map<AuditDiffBuilder.Operation, Integer> byOperation =
                new EnumMap<>(AuditDiffBuilder.Operation.class);

        private Counter(String table) {
            this.table = table;
        }

        private void increment(AuditDiffBuilder.Operation operation) {
            byOperation.merge(operation, 1, Integer::sum);
        }

        private int total() {
            return byOperation.values().stream().mapToInt(Integer::intValue).sum();
        }
    }
}
