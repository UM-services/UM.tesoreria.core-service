package um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.domain.ports.out;

import um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.domain.model.TesoreriaEstadoFacultad;

import java.math.BigDecimal;
import java.util.Optional;

public interface TesoreriaEstadoRepository {

    Optional<TesoreriaEstadoFacultad> findByUnique(Integer facultadId, BigDecimal personaId, Integer documentoId);
}
