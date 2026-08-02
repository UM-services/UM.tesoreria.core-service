package um.tesoreria.core.hexagonal.persona.domain.ports.in;

import um.tesoreria.core.hexagonal.persona.domain.model.PersonaKey;
import java.util.List;

public interface FindInscriptosSinChequeraDefaultUseCase {
    List<PersonaKey> findAllInscriptosSinChequeraDefault(Integer facultadId, Integer lectivoId, Integer geograficaId, Integer claseChequeraId, Integer curso);
}
