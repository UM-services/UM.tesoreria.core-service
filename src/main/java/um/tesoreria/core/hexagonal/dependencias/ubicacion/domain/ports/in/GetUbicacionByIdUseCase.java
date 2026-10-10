package um.tesoreria.core.hexagonal.dependencias.ubicacion.domain.ports.in;

import um.tesoreria.core.hexagonal.dependencias.ubicacion.domain.model.Ubicacion;
import java.util.Optional;

public interface GetUbicacionByIdUseCase {
    Optional<Ubicacion> getUbicacionById(Integer ubicacionId);
}
