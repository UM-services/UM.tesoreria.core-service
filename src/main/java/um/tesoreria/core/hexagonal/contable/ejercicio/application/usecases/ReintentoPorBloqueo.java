package um.tesoreria.core.hexagonal.contable.ejercicio.application.usecases;

import org.springframework.dao.PessimisticLockingFailureException;

import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

/**
 * Dos altas (o un alta y una baja) que leen el último ejercicio con bloqueo pueden quedar en deadlock de InnoDB:
 * ambas toman el bloqueo de hueco sobre el final del índice y la que inserta espera a la otra. MySQL revierte
 * una de las dos; como la transacción entera es repetible, se reintenta y la repetición ve el estado ya confirmado
 * (y responde SALTO/DUPLICADO/NO_ES_ULTIMO en lugar de un error de base de datos).
 * Solo se reintenta el deadlock, identificado por el código de error de MySQL 1213: el SQLState 40001 no sirve,
 * porque Connector/J también lo usa para el lock wait timeout (1205). Ese timeout ya esperó todo el plazo y repetirlo
 * retendría el hilo y la conexión; sube tal cual y la web lo responde 409.
 */
final class ReintentoPorBloqueo {
    private static final int INTENTOS = 3;
    private static final int ERROR_DEADLOCK = 1213;

    private ReintentoPorBloqueo() {
    }

    static <T> T ejecutar(Supplier<T> transaccion) {
        for (int intento = 1; ; intento++) {
            try {
                return transaccion.get();
            } catch (PessimisticLockingFailureException ex) {
                if (!esDeadlock(ex) || intento >= INTENTOS) {
                    throw ex;
                }
                pausar();
            }
        }
    }

    private static boolean esDeadlock(Throwable ex) {
        for (Throwable causa = ex; causa != null; causa = causa.getCause()) {
            if (causa instanceof SQLException sql && sql.getErrorCode() == ERROR_DEADLOCK) {
                return true;
            }
        }
        return false;
    }

    /** Espera corta y aleatoria para que los dos perdedores de un deadlock no vuelvan a chocar en lockstep. */
    private static void pausar() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(20, 80));
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(ex);
        }
    }
}
