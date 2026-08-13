package um.tesoreria.core.hexagonal.personas.legajo.application.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.hexagonal.personas.legajo.application.exception.LegajoException;
import um.tesoreria.core.hexagonal.personas.legajo.domain.model.Legajo;
import um.tesoreria.core.hexagonal.personas.legajo.domain.ports.in.AddLegajoUseCase;
import um.tesoreria.core.hexagonal.personas.legajo.domain.ports.in.FindAllLegajosByFacultadIdUseCase;
import um.tesoreria.core.hexagonal.personas.legajo.domain.ports.in.FindLegajoByUniqueUseCase;
import um.tesoreria.core.hexagonal.personas.legajo.domain.ports.in.SaveAllLegajosUseCase;

@Service
@RequiredArgsConstructor
public class LegajoService {

    private final FindAllLegajosByFacultadIdUseCase findAllByFacultadIdUseCase;
    private final FindLegajoByUniqueUseCase findByUniqueUseCase;
    private final SaveAllLegajosUseCase saveAllLegajosUseCase;
    private final AddLegajoUseCase addLegajoUseCase;

    public List<Legajo> findAllByFacultadId(Integer facultadId) {
        return findAllByFacultadIdUseCase.findAllByFacultadId(facultadId);
    }

    public Legajo findByFacultadIdAndPersonaIdAndDocumentoId(Integer facultadId, BigDecimal personaId,
                                                            Integer documentoId) {
        return findByUniqueUseCase.findByFacultadIdAndPersonaIdAndDocumentoId(facultadId, personaId, documentoId)
                .orElseThrow(() -> new LegajoException(facultadId, personaId, documentoId));
    }

    public List<Legajo> saveAll(List<Legajo> legajos) {
        return saveAllLegajosUseCase.saveAll(legajos);
    }

    public Legajo add(Legajo legajo) {
        return addLegajoUseCase.add(legajo);
    }

}