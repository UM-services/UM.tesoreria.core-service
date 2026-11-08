package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.RendicionPayPerTic;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.PayPerTicRendicionRepository;
import um.tesoreria.core.model.PayPerTic;
import um.tesoreria.core.repository.PayPerTicRepository;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Adaptador del slice sobre la tabla {@code paypertic}.
 *
 * <p>Reutiliza la entidad y el repositorio JPA existentes (la tabla ya está mapeada por el
 * legacy); el dominio del slice sólo ve {@link RendicionPayPerTic}.</p>
 */
@Component
@RequiredArgsConstructor
public class JpaPayPerTicRendicionRepositoryAdapter implements PayPerTicRendicionRepository {

    private final PayPerTicRepository repository;

    @Override
    public List<RendicionPayPerTic> findPendientesByPeriodo(OffsetDateTime desde, OffsetDateTime hasta) {
        return repository.findAllByPaymentdateBetween(desde, hasta).stream()
                .filter(pago -> pago.getImportado() == null || pago.getImportado() == 0)
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void marcarImportado(String payperticId) {
        PayPerTic pago = repository.findById(payperticId)
                .orElseThrow(() -> new IllegalArgumentException("No existe la rendición PayPerTIC " + payperticId));
        pago.setImportado((byte) 1);
        repository.save(pago);
    }

    private RendicionPayPerTic toDomain(PayPerTic pago) {
        return new RendicionPayPerTic(
                pago.getPayperticId(),
                pago.getExternaltransactionId(),
                pago.getAmount(),
                pago.getPaymentdate(),
                pago.getAccreditationdate(),
                pago.getDuedate());
    }

}
