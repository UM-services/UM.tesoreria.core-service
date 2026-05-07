package um.tesoreria.core.hexagonal.dependencia.domain.ports.in;
import um.tesoreria.core.hexagonal.dependencia.domain.model.Dependencia;
import java.util.Optional;
public interface GetDependenciaByIdUseCase { Optional<Dependencia> getById(Integer id); }
