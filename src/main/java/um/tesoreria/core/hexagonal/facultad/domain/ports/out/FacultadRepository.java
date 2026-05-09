package um.tesoreria.core.hexagonal.facultad.domain.ports.out;

import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import java.util.List;
import java.util.Optional;

public interface FacultadRepository {
    List<Facultad> findAll();
    List<Facultad> findAllIn(List<Integer> ids);
    Optional<Facultad> findById(Integer facultadId);
}
