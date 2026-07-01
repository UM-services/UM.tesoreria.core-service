package um.tesoreria.core.hexagonal.lectivo.domain.ports.in;

import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import java.math.BigDecimal;
import java.util.List;

public interface GetAllLectivosByPersonaUseCase {
    List<Lectivo> getAllLectivosByPersona(BigDecimal personaId, Integer documentoId);
}
