package um.tesoreria.core.hexagonal.domicilio.domain.ports.out;

import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.model.view.DomicilioKey;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface DomicilioRepository {
    Domicilio create(Domicilio domicilio);
    Optional<Domicilio> findById(Long id);
    Optional<Domicilio> findByUnique(BigDecimal personaId, Integer documentoId);
    Optional<Domicilio> findFirstByPersonaId(BigDecimal personaId);
    Optional<Domicilio> update(Long id, Domicilio domicilio);
    boolean deleteById(Long id);
}
