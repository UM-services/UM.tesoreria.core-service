package um.tesoreria.core.hexagonal.dependencias.dependencia.domain.ports.in;
import um.tesoreria.core.hexagonal.dependencias.dependencia.domain.model.Dependencia;
import java.util.Optional;
public interface GetDependenciaByIdUseCase { Optional<Dependencia> getById(Integer id); }
