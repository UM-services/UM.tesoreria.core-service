package um.tesoreria.core.hexagonal.persona.domain.ports.out;

import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface PersonaRepository {
    Optional<Persona> findByPersonaIdAndDocumentoId(BigDecimal personaId, Integer documentoId);
    Optional<Persona> findTopByPersonaId(BigDecimal personaId);
    Optional<Persona> findByUniqueId(Long uniqueId);
    List<Persona> findAllByCbuLike(String cbu);
    Persona save(Persona persona);
}
