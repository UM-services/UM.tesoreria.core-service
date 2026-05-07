package um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out;

import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import java.util.List;
import java.util.Optional;

public interface UbicacionArticuloRepository {
    UbicacionArticulo save(UbicacionArticulo ubicacionArticulo);
    List<UbicacionArticulo> findAll();
    Optional<UbicacionArticulo> findByUbicacionIdAndArticuloId(Integer ubicacionId, Long articuloId);
    List<UbicacionArticulo> findAllByArticuloId(Long articuloId);
}
