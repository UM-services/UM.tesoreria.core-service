package um.tesoreria.core.hexagonal.persona.domain.ports.in;

import um.tesoreria.core.hexagonal.persona.domain.model.PersonaKey;
import java.util.List;

public interface FindInscriptosSinChequeraUseCase {
    List<PersonaKey> findAllInscriptosSinChequera(Integer facultadId, Integer lectivoId, Integer geograficaId, Integer curso);
}
