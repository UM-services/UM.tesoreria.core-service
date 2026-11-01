package um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.InscripcionMaterias;
import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.ports.in.FindInscripcionMateriasUseCase;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PendienteInfoFacultadService {

    private final FindInscripcionMateriasUseCase findInscripcionMateriasUseCase;

    public Optional<InscripcionMaterias> findByAlumno(Integer facultadId, Integer lectivoId, Integer geograficaId,
                                                      BigDecimal personaId, Integer documentoId) {
        return findInscripcionMateriasUseCase.findByAlumno(facultadId, lectivoId, geograficaId, personaId, documentoId);
    }
}
