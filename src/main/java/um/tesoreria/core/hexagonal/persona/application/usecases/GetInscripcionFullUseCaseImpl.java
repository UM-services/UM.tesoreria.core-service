package um.tesoreria.core.hexagonal.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.extern.consumer.InscripcionFacultadConsumer;
import um.tesoreria.core.extern.model.dto.InscripcionFullDto;
import um.tesoreria.core.hexagonal.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.persona.domain.ports.in.GetInscripcionFullUseCase;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class GetInscripcionFullUseCaseImpl implements GetInscripcionFullUseCase {

    private final FacultadService facultadService;
    private final InscripcionFacultadConsumer inscripcionFacultadConsumer;

    @Override
    public InscripcionFullDto findInscripcionFull(Integer facultadId, BigDecimal personaId, Integer documentoId,
            Integer lectivoId) {
        Facultad facultad = facultadService.findByFacultadId(facultadId);
        return inscripcionFacultadConsumer.findInscripcionFull(facultad.getApiserver(), facultad.getApiport(),
                facultadId, personaId, documentoId, lectivoId);
    }
}
