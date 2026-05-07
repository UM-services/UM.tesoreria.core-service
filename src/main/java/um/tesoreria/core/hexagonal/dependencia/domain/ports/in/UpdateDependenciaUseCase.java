package um.tesoreria.core.hexagonal.dependencia.domain.ports.in;
import um.tesoreria.core.hexagonal.dependencia.domain.model.Dependencia;
public interface UpdateDependenciaUseCase { Dependencia update(Integer id, Dependencia data); }
