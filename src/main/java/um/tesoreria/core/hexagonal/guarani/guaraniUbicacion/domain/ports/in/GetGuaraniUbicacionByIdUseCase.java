package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.in;

import java.util.Optional;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.model.GuaraniUbicacion;

public interface GetGuaraniUbicacionByIdUseCase {
    Optional<GuaraniUbicacion> getById(Integer id);
}
