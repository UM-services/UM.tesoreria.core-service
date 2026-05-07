package um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in;

import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import java.util.Optional;

public interface GetUbicacionArticuloUseCase {
    Optional<UbicacionArticulo> getByUbicacionAndArticulo(Integer ubicacionId, Long articuloId);
}
