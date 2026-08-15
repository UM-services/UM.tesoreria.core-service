package um.tesoreria.core.hexagonal.personas.legajo.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.legajo.domain.model.Legajo;

import java.math.BigDecimal;
import java.util.Optional;

public interface FindLegajoByUniqueUseCase {
    Optional<Legajo> findByFacultadIdAndPersonaIdAndDocumentoId(Integer facultadId, BigDecimal personaId, Integer documentoId);
}