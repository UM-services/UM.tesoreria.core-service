package um.tesoreria.core.hexagonal.dependencia.domain.ports.in;
import um.tesoreria.core.hexagonal.dependencia.domain.model.Dependencia;
import java.util.List;
public interface GetAllDependenciasUseCase { List<Dependencia> getAll(); }
