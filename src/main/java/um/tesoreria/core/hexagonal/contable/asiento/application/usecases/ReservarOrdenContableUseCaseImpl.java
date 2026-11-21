package um.tesoreria.core.hexagonal.contable.asiento.application.usecases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.transaction.support.TransactionTemplate;
import um.tesoreria.core.hexagonal.contable.asiento.application.exception.AsientoSecuenciaException;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoSecuencia;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ReservarOrdenContableUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.out.AsientoOrdenMaximoRepository;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.out.AsientoSecuenciaRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * Reserva del orden contable en una transacción propia ({@code REQUIRES_NEW}).
 *
 * <p>La reserva se confirma antes de que el llamador escriba la cabecera o {@code movcon}. Así el
 * número queda tomado aunque el llamador tarde (o falle) y ningún otro proceso puede obtenerlo.
 * Si dos procesos reservan a la vez para una fecha nueva, uno gana el INSERT y el otro reintenta
 * con la fila ya creada; el lock pesimista serializa el resto.</p>
 */
@Component
public class ReservarOrdenContableUseCaseImpl implements ReservarOrdenContableUseCase {

    private static final int INTENTOS = 5;

    private final AsientoSecuenciaRepository secuenciaRepository;
    private final AsientoOrdenMaximoRepository maximoRepository;
    private final TransactionOperations transaccion;

    @Autowired
    public ReservarOrdenContableUseCaseImpl(AsientoSecuenciaRepository secuenciaRepository,
                                            AsientoOrdenMaximoRepository maximoRepository,
                                            PlatformTransactionManager transactionManager) {
        this(secuenciaRepository, maximoRepository, nuevaTransaccion(transactionManager));
    }

    ReservarOrdenContableUseCaseImpl(AsientoSecuenciaRepository secuenciaRepository,
                                     AsientoOrdenMaximoRepository maximoRepository,
                                     TransactionOperations transaccion) {
        this.secuenciaRepository = secuenciaRepository;
        this.maximoRepository = maximoRepository;
        this.transaccion = transaccion;
    }

    @Override
    public Integer reservar(OffsetDateTime fecha) {
        OffsetDateTime normalizada = normalizar(fecha);
        RuntimeException ultimoError = null;
        for (int intento = 0; intento < INTENTOS; intento++) {
            try {
                return Objects.requireNonNull(transaccion.execute(status -> reservarEnTransaccion(normalizada)));
            } catch (DataIntegrityViolationException | PessimisticLockingFailureException e) {
                // Otra transacción creó/actualizó la fila: se reintenta sobre el estado nuevo.
                ultimoError = e;
            }
        }
        throw new AsientoSecuenciaException(
                "No se pudo reservar el orden contable para la fecha " + normalizada.toLocalDate(), ultimoError);
    }

    private Integer reservarEnTransaccion(OffsetDateTime fecha) {
        AsientoSecuencia secuencia = secuenciaRepository.findWithLockByFecha(fecha)
                .orElseGet(() -> secuenciaRepository.save(
                        new AsientoSecuencia(fecha, maximoRepository.maxOrden(fecha))));
        return secuenciaRepository.save(
                new AsientoSecuencia(fecha, secuencia.ultimoOrden() + 1)).ultimoOrden();
    }

    /**
     * No abre transacción propia: participa de la del llamador para que el alta del asiento y el
     * avance de la secuencia sean atómicos.
     */
    @Override
    public void registrarUso(OffsetDateTime fecha, Integer orden) {
        if (orden == null) {
            return;
        }
        OffsetDateTime normalizada = normalizar(fecha);
        AsientoSecuencia secuencia = secuenciaRepository.findWithLockByFecha(normalizada)
                .orElseGet(() -> secuenciaRepository.save(new AsientoSecuencia(normalizada,
                        Math.max(orden, maximoRepository.maxOrden(normalizada)))));
        if (orden > secuencia.ultimoOrden()) {
            secuenciaRepository.save(new AsientoSecuencia(normalizada, orden));
        }
    }

    private static TransactionOperations nuevaTransaccion(PlatformTransactionManager transactionManager) {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return template;
    }

    private static OffsetDateTime normalizar(OffsetDateTime fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha contable es obligatoria");
        }
        return fecha.withOffsetSameInstant(ZoneOffset.UTC)
                .toLocalDate()
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);
    }

}
