package um.tesoreria.core.hexagonal.dependencia.domain.ports.out;

import um.tesoreria.core.hexagonal.dependencia.domain.model.Dependencia;
import java.util.List;
import java.util.Optional;

public interface DependenciaRepository {
    List<Dependencia> findAll();
    Optional<Dependencia> findById(Integer dependenciaId);
    Dependencia save(Dependencia dependencia);
}
