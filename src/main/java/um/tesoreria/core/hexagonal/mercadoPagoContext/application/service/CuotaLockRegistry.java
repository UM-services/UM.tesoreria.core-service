package um.tesoreria.core.hexagonal.mercadoPagoContext.application.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

/**
 * Serializa, dentro de la instancia, el trabajo por cuota en el circuito de Mercado Pago.
 *
 * <p>Sin esto, dos GET {@code deudaextended} concurrentes para la misma persona cargan el
 * contexto antes de que el primero lo persista y crean dos preferencias (la segunda pisa a la
 * primera). El lock es reentrante para permitir anidar el flujo de contexto + preferencia.</p>
 */
@Component
public class CuotaLockRegistry {

    private static final int STRIPES = 256;

    private final Lock[] locks = new Lock[STRIPES];

    public CuotaLockRegistry() {
        for (int index = 0; index < STRIPES; index++) {
            locks[index] = new ReentrantLock();
        }
    }

    public <T> T withLock(Long key, Supplier<T> action) {
        Lock lock = lockFor(key);
        lock.lock();
        try {
            return action.get();
        } finally {
            lock.unlock();
        }
    }

    private Lock lockFor(Long key) {
        int hash = key == null ? 0 : key.hashCode();
        return locks[Math.floorMod(hash, STRIPES)];
    }

}
