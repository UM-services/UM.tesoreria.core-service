package um.tesoreria.core.hexagonal.ubicacion.domain.ports.in;

import um.tesoreria.core.hexagonal.ubicacion.domain.model.Ubicacion;
import java.util.List;

public interface GetAllUbicacionesUseCase {
    List<Ubicacion> getAllUbicaciones();
}
