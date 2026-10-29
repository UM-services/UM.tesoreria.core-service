package um.tesoreria.core.audit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.FlushEvent;
import org.hibernate.event.spi.FlushEventListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Vuelca el buffer de auditoría al outbox apenas Hibernate termina un flush.
 *
 * <p>Hace falta además del volcado de {@link AuditTransactionSynchronization#beforeCommit}
 * porque no todo cambio se flushea antes del commit: un delete derivado (sin
 * {@code @Modifying}) o un {@code deleteById} sólo ejecutan su SQL en el flush final, y en ese
 * caso el listener de entidad emite {@code PostDelete} <b>después</b> de que
 * {@code beforeCommit} ya drenó el buffer; el evento se perdía. Al escribir en el flush, el
 * evento siempre entra en la misma transacción y con la misma conexión que el cambio.</p>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditFlushListener implements FlushEventListener {

    private final AuditProperties properties;
    private final AuditBuffer buffer;
    private final AuditOutboxWriter writer;

    @Override
    public void onFlush(FlushEvent event) throws HibernateException {
        drain(event.getSession());
    }

    void drain(SharedSessionContractImplementor session) {
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
}
