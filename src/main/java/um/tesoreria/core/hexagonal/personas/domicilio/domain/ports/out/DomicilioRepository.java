package um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.out;

import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;

import java.math.BigDecimal;
import java.util.Optional;

public interface DomicilioRepository {
    Domicilio create(Domicilio domicilio);
    Optional<Domicilio> findById(Long id);
    Optional<Domicilio> findByUnique(BigDecimal personaId, Integer documentoId);
    Optional<Domicilio> findFirstByPersonaId(BigDecimal personaId);
    Optional<Domicilio> update(Long id, Domicilio domicilio);
    boolean deleteById(Long id);
}
