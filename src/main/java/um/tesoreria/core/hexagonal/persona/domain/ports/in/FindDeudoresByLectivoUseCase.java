package um.tesoreria.core.hexagonal.persona.domain.ports.in;

import um.tesoreria.core.model.view.PersonaKey;
import java.util.List;

public interface FindDeudoresByLectivoUseCase {
    List<PersonaKey> findAllDeudorByLectivoId(Integer facultadId, Integer lectivoId, Integer geograficaId, Integer cuotas);
}
