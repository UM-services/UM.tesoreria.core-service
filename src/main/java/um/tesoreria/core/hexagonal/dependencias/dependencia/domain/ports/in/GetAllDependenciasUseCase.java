package um.tesoreria.core.hexagonal.dependencias.dependencia.domain.ports.in;
import um.tesoreria.core.hexagonal.dependencias.dependencia.domain.model.Dependencia;
import java.util.List;
public interface GetAllDependenciasUseCase { List<Dependencia> getAll(); }
