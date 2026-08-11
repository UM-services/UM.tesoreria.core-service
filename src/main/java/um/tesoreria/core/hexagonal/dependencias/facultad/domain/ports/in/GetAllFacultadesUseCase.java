package um.tesoreria.core.hexagonal.dependencias.facultad.domain.ports.in;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import java.util.List;
public interface GetAllFacultadesUseCase { List<Facultad> getAll(); }
