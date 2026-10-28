package um.tesoreria.core.audit;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Buffer de eventos de la transacción en curso. Es un ThreadLocal: cada request/TX
 * tiene el suyo.
 */
@Component
public class AuditBuffer {

    private final ThreadLocal<List<AuditEventPayload>> events = ThreadLocal.withInitial(ArrayList::new);
    private final ThreadLocal<Boolean> synchronizationRegistered = ThreadLocal.withInitial(() -> false);

    public void add(AuditEventPayload payload) {
        events.get().add(payload);
    }

    public List<AuditEventPayload> drain() {
        List<AuditEventPayload> current = events.get();
        List<AuditEventPayload> copy = new ArrayList<>(current);
        current.clear();
        return copy;
    }

    /**
     * @return true sólo la primera vez por transacción, para registrar una única
     *         sincronización de commit.
     */
    public boolean markSynchronizationRegistered() {
        if (Boolean.TRUE.equals(synchronizationRegistered.get())) {
            return false;
        }
        synchronizationRegistered.set(true);
        return true;
    }

    public int size() {
        return events.get().size();
    }

    public void clear() {
        events.get().clear();
        synchronizationRegistered.set(false);
    }
}
