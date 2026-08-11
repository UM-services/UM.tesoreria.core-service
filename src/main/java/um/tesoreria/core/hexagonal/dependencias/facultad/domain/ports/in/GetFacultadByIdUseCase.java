package um.tesoreria.core.hexagonal.dependencias.facultad.domain.ports.in;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import java.util.Optional;
public interface GetFacultadByIdUseCase { Optional<Facultad> getById(Integer id); }
