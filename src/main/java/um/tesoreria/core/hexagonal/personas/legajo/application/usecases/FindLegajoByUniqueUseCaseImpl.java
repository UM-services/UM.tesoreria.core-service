package um.tesoreria.core.hexagonal.personas.legajo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.legajo.domain.model.Legajo;
import um.tesoreria.core.hexagonal.personas.legajo.domain.ports.in.FindLegajoByUniqueUseCase;
import um.tesoreria.core.hexagonal.personas.legajo.domain.ports.out.LegajoRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FindLegajoByUniqueUseCaseImpl implements FindLegajoByUniqueUseCase {

    private final LegajoRepository legajoRepository;

    @Override
    public Optional<Legajo> findByFacultadIdAndPersonaIdAndDocumentoId(Integer facultadId, BigDecimal personaId, Integer documentoId) {
        return legajoRepository.findByFacultadIdAndPersonaIdAndDocumentoId(facultadId, personaId, documentoId);
    }
}