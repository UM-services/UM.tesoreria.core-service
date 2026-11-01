package um.tesoreria.core.audit;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Punto de entrada para auditar una operación masiva como resumen.
 *
 * <pre>{@code
 * try (AuditBulkScope ignored = auditBulk.begin(BulkContext.of("generacion-cuotas", anho, mes, null))) {
 *     chequeraCuotaRepository.saveAll(cuotas);
 * }
 * }</pre>
 *
 * <p>Requiere una transacción activa (el resumen se escribe en el outbox junto con los
 * cambios). Si la captura está apagada devuelve un scope inerte, así el código de negocio no
 * necesita condiciones.</p>
 */
@Component
@RequiredArgsConstructor
public class AuditBulkService {

    private final AuditProperties properties;
    private final AuditBuffer buffer;
    private final AuditOutboxWriter writer;
    private final EntityManager entityManager;

    public AuditBulkScope begin(BulkContext context) {
        if (!properties.isBulkEnabled()
                || !properties.isChangeEvents()
                || properties.getMode() == AuditProperties.Mode.OFF) {
            return AuditBulkScope.disabled();
        }
        if (AuditBulkScope.current() != null) {
            throw new IllegalStateException("Ya hay un scope de auditoría masiva activo en este hilo");
        }
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            throw new IllegalStateException("AuditBulkScope requiere una transacción activa");
        }

        // Sin esto, un scope que sólo resume (sin eventos por fila) no tendría quién
        // vuelque el resumen al outbox en beforeCommit.
        if (buffer.markSynchronizationRegistered()) {
            SessionImplementor session = entityManager.unwrap(SessionImplementor.class);
            TransactionSynchronizationManager.registerSynchronization(
                    new AuditTransactionSynchronization(session, buffer, writer, properties));
        }

        return AuditBulkScope.open(context, properties.getSummaryEntities(), buffer,
                entityManager.unwrap(SessionImplementor.class));
    }
}
