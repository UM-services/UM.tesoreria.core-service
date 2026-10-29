package um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.InscripcionMaterias;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.ports.in.FindInscripcionMateriasUseCase;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.ports.out.PendienteInfoRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FindInscripcionMateriasUseCaseImpl implements FindInscripcionMateriasUseCase {

    private final PendienteInfoRepository pendienteInfoRepository;

    @Override
    public Optional<InscripcionMaterias> findByAlumno(Integer facultadId, Integer lectivoId, Integer geograficaId,
                                                      BigDecimal personaId, Integer documentoId) {
        return pendienteInfoRepository.findByAlumno(facultadId, lectivoId, geograficaId, personaId, documentoId);
    }
}
