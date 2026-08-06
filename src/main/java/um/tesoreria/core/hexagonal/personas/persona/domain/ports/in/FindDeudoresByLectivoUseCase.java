package um.tesoreria.core.hexagonal.personas.persona.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaKey;
import java.util.List;

public interface FindDeudoresByLectivoUseCase {
    List<PersonaKey> findAllDeudorByLectivoId(Integer facultadId, Integer lectivoId, Integer geograficaId, Integer cuotas);
}
