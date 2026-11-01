package um.tesoreria.core.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.springframework.transaction.support.TransactionSynchronization;

import java.util.List;

/**
 * Vuelca el buffer al outbox dentro de la misma transacción, justo antes del commit.
 *
 * <p>Se hace acá y no en un "post-flush" porque Hibernate 7 ya no expone
 * {@code PostFlushEventListener}. Antes de volcar se fuerza un flush para que los
 * listeners de entidad hayan emitido todos sus eventos.</p>
 */
@Slf4j
@RequiredArgsConstructor
public class AuditTransactionSynchronization implements TransactionSynchronization {

    private final SharedSessionContractImplementor session;
    private final AuditBuffer buffer;
    private final AuditOutboxWriter writer;
    private final AuditProperties properties;

    @Override
    public void beforeCommit(boolean readOnly) {
        if (readOnly) {
            return;
        }
        if (session.isOpen()) {
            session.flush();
        }
        List<AuditEventPayload> events = buffer.drain();
        if (events.isEmpty()) {
            return;
        }
        if (properties.getMode() == AuditProperties.Mode.SEND) {
            writer.write(session, events);
        } else {
            log.info("auditoría (modo LOG): {} evento(s) capturado(s), no enviados", events.size());
        }
    }

    @Override
    public void afterCompletion(int status) {
        buffer.clear();
    }
}
