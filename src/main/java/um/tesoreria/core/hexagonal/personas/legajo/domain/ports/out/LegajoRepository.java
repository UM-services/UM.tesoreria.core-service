package um.tesoreria.core.hexagonal.personas.legajo.domain.ports.out;

import um.tesoreria.core.hexagonal.personas.legajo.domain.model.Legajo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface LegajoRepository {
    List<Legajo> findAllByFacultadId(Integer facultadId);

    Optional<Legajo> findByFacultadIdAndPersonaIdAndDocumentoId(Integer facultadId, BigDecimal personaId, Integer documentoId);

    List<Legajo> saveAll(List<Legajo> legajos);

    Legajo save(Legajo legajo);
}