package um.tesoreria.core.hexagonal.cursoCargoContratado.domain.ports.in;

import um.tesoreria.core.hexagonal.cursoCargoContratado.domain.model.CursoCargoContratado;

import java.math.BigDecimal;
import java.util.List;

public interface GetAllCargosByPersonaUseCase {

    List<CursoCargoContratado> getAllCargosByPersona(Long contratoId, Integer anho, Integer mes, BigDecimal personaId, Integer documentoId);

}
