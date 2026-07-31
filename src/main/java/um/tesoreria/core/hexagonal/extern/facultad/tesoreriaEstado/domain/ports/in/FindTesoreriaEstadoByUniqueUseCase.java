package um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.domain.ports.in;

import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.domain.model.TesoreriaEstadoFacultad;

import java.math.BigDecimal;
import java.util.Optional;

public interface FindTesoreriaEstadoByUniqueUseCase {

    Optional<TesoreriaEstadoFacultad> findByUnique(Integer facultadId, BigDecimal personaId, Integer documentoId);
}
