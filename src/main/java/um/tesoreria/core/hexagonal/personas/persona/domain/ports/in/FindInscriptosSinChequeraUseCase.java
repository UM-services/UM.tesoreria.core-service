package um.tesoreria.core.hexagonal.personas.persona.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaKey;
import java.util.List;

public interface FindInscriptosSinChequeraUseCase {
    List<PersonaKey> findAllInscriptosSinChequera(Integer facultadId, Integer lectivoId, Integer geograficaId, Integer curso);
}
