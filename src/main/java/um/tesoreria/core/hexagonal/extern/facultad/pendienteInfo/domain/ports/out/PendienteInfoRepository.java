package um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.ports.out;

import um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model.InscripcionMaterias;

import java.math.BigDecimal;
import java.util.Optional;

public interface PendienteInfoRepository {

    /** Vacío si el alumno no tiene inscripción o no tiene materias inscriptas. */
    Optional<InscripcionMaterias> findByAlumno(Integer facultadId, Integer lectivoId, Integer geograficaId,
                                               BigDecimal personaId, Integer documentoId);
}
