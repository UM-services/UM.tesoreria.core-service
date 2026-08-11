package um.tesoreria.core.hexagonal.dependencias.facultad.domain.ports.out;

import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import java.util.List;
import java.util.Optional;

public interface FacultadRepository {
    List<Facultad> findAll();
    List<Facultad> findAllIn(List<Integer> ids);
    List<Facultad> findAllByGuaraniResponsableAcademicaNotNull();
    Optional<Facultad> findById(Integer facultadId);
    Optional<Facultad> findByGuaraniResponsableAcademica(Integer responsableAcademica);
}
