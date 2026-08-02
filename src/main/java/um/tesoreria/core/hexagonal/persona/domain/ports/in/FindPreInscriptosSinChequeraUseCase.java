package um.tesoreria.core.hexagonal.persona.domain.ports.in;

import um.tesoreria.core.hexagonal.persona.domain.model.PersonaKey;
import java.util.List;

public interface FindPreInscriptosSinChequeraUseCase {
    List<PersonaKey> findAllPreInscriptosSinChequera(Integer facultadId, Integer lectivoId, Integer geograficaId);
}
