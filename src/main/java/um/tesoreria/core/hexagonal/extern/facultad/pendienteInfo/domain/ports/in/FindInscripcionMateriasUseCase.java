package um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.ports.in;

import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.InscripcionMaterias;

import java.math.BigDecimal;
import java.util.Optional;

public interface FindInscripcionMateriasUseCase {

    /** Vacío si el alumno no tiene inscripción o no tiene materias inscriptas. */
    Optional<InscripcionMaterias> findByAlumno(Integer facultadId, Integer lectivoId, Integer geograficaId,
                                               BigDecimal personaId, Integer documentoId);
}
