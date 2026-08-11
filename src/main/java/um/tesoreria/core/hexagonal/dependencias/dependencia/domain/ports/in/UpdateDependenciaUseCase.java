package um.tesoreria.core.hexagonal.dependencias.dependencia.domain.ports.in;
import um.tesoreria.core.hexagonal.dependencias.dependencia.domain.model.Dependencia;
public interface UpdateDependenciaUseCase { Dependencia update(Integer id, Dependencia data); }
