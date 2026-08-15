package um.tesoreria.core.hexagonal.personas.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.extern.consumer.InscripcionFacultadConsumer;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.InscripcionFullDto;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.in.GetInscripcionFullUseCase;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class GetInscripcionFullUseCaseImpl implements GetInscripcionFullUseCase {

    private final InscripcionFacultadConsumer inscripcionFacultadConsumer;

    @Override
    public InscripcionFullDto findInscripcionFull(Integer facultadId, BigDecimal personaId, Integer documentoId,
            Integer lectivoId) {
        return inscripcionFacultadConsumer.findInscripcionFull(facultadId, personaId, documentoId, lectivoId);
    }
}
