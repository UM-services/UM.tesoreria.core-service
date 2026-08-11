package um.tesoreria.core.hexagonal.dependencias.dependencia.domain.ports.out;

import um.tesoreria.core.hexagonal.dependencias.dependencia.domain.model.Dependencia;
import java.util.List;
import java.util.Optional;

public interface DependenciaRepository {
    List<Dependencia> findAll();
    Optional<Dependencia> findById(Integer dependenciaId);
    Dependencia save(Dependencia dependencia);
}
