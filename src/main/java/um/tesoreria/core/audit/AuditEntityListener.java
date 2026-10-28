package um.tesoreria.core.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuditEntityListener implements PostInsertEventListener,
        PostUpdateEventListener, PostDeleteEventListener {

    private final AuditProperties properties;
    private final AuditDiffBuilder diffBuilder;
    private final AuditBuffer buffer;
    private final AuditOutboxWriter writer;

    @Override
    public void onPostInsert(PostInsertEvent event) {
        handle(event.getEntity(), event.getId(), event.getPersister(), null, event.getState(),
                AuditDiffBuilder.Operation.INSERT, event.getSession());
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        handle(event.getEntity(), event.getId(), event.getPersister(), event.getOldState(),
                event.getState(), AuditDiffBuilder.Operation.UPDATE, event.getSession());
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        handle(event.getEntity(), event.getId(), event.getPersister(), event.getDeletedState(),
                null, AuditDiffBuilder.Operation.DELETE, event.getSession());
    }

    private void handle(Object entity, Object id, EntityPersister persister,
                        Object[] beforeState, Object[] afterState,
                        AuditDiffBuilder.Operation operation,
                        SharedSessionContractImplementor session) {
        if (properties.getMode() == AuditProperties.Mode.OFF || !properties.isChangeEvents()) {
            return;
        }

        String entityName = simpleName(persister.getEntityName());
        if (properties.getExcludedEntities().contains(entityName)) {
            return;
        }

        AuditBulkScope scope = AuditBulkScope.current();
        if (scope != null && properties.getSummaryEntities().contains(entityName)) {
            // Dentro de un scope masivo no se serializa el diff: se cuenta y el resumen se
            // emite al cerrar. Fuera del scope, la misma entidad emite su CHANGE normal.
            scope.count(entityName, safeTable(persister), operation);
            AuditContext.incrementChanges();
            return;
        }

        AuditDiffBuilder.DiffResult diff = diffBuilder.build(persister, beforeState, afterState, operation);
        if (diff.changedFields().isEmpty()) {
            return;
        }

        AuditEventPayload payload = payload(entityName, id, persister, operation, diff);
        buffer.add(payload);
        AuditContext.incrementChanges();

        if (!buffer.markSynchronizationRegistered()) {
            return;
        }

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new AuditTransactionSynchronization(session, buffer, writer, properties));
        } else {
            // Sin transacción de Spring (caso raro): se escribe inmediatamente.
            List<AuditEventPayload> pending = buffer.drain();
            if (properties.getMode() == AuditProperties.Mode.SEND) {
                writer.write(session, pending);
            } else {
                log.info("auditoría (modo LOG): {} evento(s) sin transacción", pending.size());
            }
            buffer.clear();
        }
    }

    private AuditEventPayload payload(String entityName, Object id, EntityPersister persister,
                                      AuditDiffBuilder.Operation operation,
                                      AuditDiffBuilder.DiffResult diff) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("entityName", persister.getEntityName());
        metadata.put("table", safeTable(persister));
        if (diff.truncated()) {
            metadata.put("truncated", true);
        }

        return AuditEventPayload.builder()
                .eventId(UUID.randomUUID().toString())
                .occurredAt(OffsetDateTime.now(ZoneOffset.UTC))
                .correlationId(AuditContext.correlationId())
                .service("tesoreria-core")
                .level("CHANGE")
                .operation(operation.name())
                .entity(entityName)
                .entityId(id == null ? null : id.toString())
                .actorType(AuditContext.actorType())
                .actorId(AuditContext.actorId())
                .actorLogin(AuditContext.actorLogin())
                .actorTrust(AuditContext.actorTrust())
                .clientIp(AuditContext.clientIp())
                .httpMethod(AuditContext.httpMethod())
                .endpoint(AuditContext.endpoint())
                .outcome("OK")
                .httpStatus(null)
                .affectedRows(1)
                .changedFields(diff.changedFields())
                .beforeData(diff.before())
                .afterData(diff.after())
                .metadata(metadata)
                .build();
    }

    private String safeTable(EntityPersister persister) {
        try {
            return persister.getTableName();
        } catch (RuntimeException e) {
            return persister.getEntityName();
        }
    }

    private String simpleName(String entityName) {
        int index = entityName.lastIndexOf('.');
        return index < 0 ? entityName : entityName.substring(index + 1);
    }
}
