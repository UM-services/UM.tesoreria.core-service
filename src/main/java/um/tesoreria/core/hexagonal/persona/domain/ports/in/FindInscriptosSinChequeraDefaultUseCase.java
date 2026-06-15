package um.tesoreria.core.hexagonal.persona.domain.ports.in;

import um.tesoreria.core.model.view.PersonaKey;
import java.util.List;

public interface FindInscriptosSinChequeraDefaultUseCase {
    List<PersonaKey> findAllInscriptosSinChequeraDefault(Integer facultadId, Integer lectivoId, Integer geograficaId, Integer claseChequeraId, Integer curso);
}
