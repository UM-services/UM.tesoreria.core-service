package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.in;

import java.util.List;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.model.GuaraniUbicacion;

public interface GetAllGuaraniUbicacionesUseCase {
    List<GuaraniUbicacion> getAll();
}
