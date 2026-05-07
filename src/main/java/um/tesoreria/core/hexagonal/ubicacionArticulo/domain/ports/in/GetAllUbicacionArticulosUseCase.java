package um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in;

import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import java.util.List;

public interface GetAllUbicacionArticulosUseCase {
    List<UbicacionArticulo> getAll();
}
