package um.tesoreria.core.hexagonal.compras.correlativo;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Supplier;

/**
 * Reserva atómica del correlativo anual de compras, sin SQL escrito a mano.
 *
 * <p>Dos pasos:</p>
 * <ol>
 *   <li><b>Asegurar la fila del ejercicio</b> en una transacción propia ({@code REQUIRES_NEW}).
 *       Sólo ocurre la primera vez de cada ejercicio; la carrera entre dos primeras reservas la
 *       resuelve la clave primaria (el perdedor ignora la violación y sigue).</li>
 *   <li><b>Incrementar con lock pesimista</b> en la transacción del llamador: si la operación de
 *       negocio hace rollback, el número vuelve (no deja huecos de numeración) y el bloqueo dura
 *       lo que dure esa transacción, igual que el {@code INSERT ... ON DUPLICATE KEY UPDATE}
 *       anterior.</li>
 * </ol>
 *
 * <p>El paso 1 va primero a propósito: un {@code SELECT ... FOR UPDATE} sobre una fila
 * inexistente toma un gap lock que bloquearía el INSERT de la transacción propia.</p>
 */
@Slf4j
@Repository
public class ReservaCorrelativoAnual {

    private final EntityManager entityManager;
    private final TransactionOperations transaccionPropia;

    @Autowired
    public ReservaCorrelativoAnual(EntityManager entityManager, PlatformTransactionManager transactionManager) {
        this(entityManager, nuevaTransaccion(transactionManager));
    }

    ReservaCorrelativoAnual(EntityManager entityManager, TransactionOperations transaccionPropia) {
        this.entityManager = entityManager;
        this.transaccionPropia = transaccionPropia;
    }

    /**
     * Reserva y devuelve el siguiente correlativo del ejercicio. Si es la primera reserva del
     * ejercicio, arranca en {@code 1}.
     */
    public <T extends SecuenciaAnual> int reservar(SecuenciaAnualRepository<T> repository,
                                                   Integer ejercicioId, Supplier<T> nueva) {
        RuntimeException falloDelAsegurado = asegurar(repository, ejercicioId, nueva);
        T secuencia = repository.findWithLockByEjercicioId(ejercicioId)
                .orElseThrow(() -> new IllegalStateException(
                        "No se pudo asegurar el correlativo del ejercicio " + ejercicioId, falloDelAsegurado));
        int siguiente = secuencia.getUltimoNumero() == null ? 1 : secuencia.getUltimoNumero() + 1;
        secuencia.setUltimoNumero(siguiente);
        // Entidad administrada por el contexto de persistencia: el UPDATE sale al commit del llamador.
        return siguiente;
    }

    /**
     * Crea la fila del ejercicio si no existe. Devuelve el fallo (o {@code null}) en lugar de
     * propagarlo: la única causa esperable es que otra transacción la haya creado en el medio
     * (clave primaria); lo decide la lectura con lock que viene después. Si la fila no existe y
     * hubo fallo, {@link #reservar} lo propaga como causa.
     */
    private <T extends SecuenciaAnual> RuntimeException asegurar(SecuenciaAnualRepository<T> repository,
                                                                 Integer ejercicioId, Supplier<T> nueva) {
        try {
            transaccionPropia.executeWithoutResult(status -> {
                if (repository.existsById(ejercicioId)) {
                    return;
                }
                entityManager.persist(nueva.get());
                entityManager.flush();
            });
            return null;
        } catch (RuntimeException e) {
            log.debug("No se pudo crear la fila del correlativo del ejercicio {}: {}", ejercicioId, e.getMessage());
            return e;
        }
    }

    private static TransactionOperations nuevaTransaccion(PlatformTransactionManager transactionManager) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return template;
    }

}
