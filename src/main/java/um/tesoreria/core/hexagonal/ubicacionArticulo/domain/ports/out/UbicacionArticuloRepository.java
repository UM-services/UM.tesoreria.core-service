package um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out;

import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.AsignacionGuardada;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import java.util.List;
import java.util.Optional;

public interface UbicacionArticuloRepository {
    /** Upsert por par (ubicación, artículo); devuelve también cómo estaba el vínculo, si existía. */
    AsignacionGuardada save(UbicacionArticulo ubicacionArticulo);
    List<UbicacionArticulo> findAll();
    Optional<UbicacionArticulo> findByUbicacionIdAndArticuloId(Integer ubicacionId, Long articuloId);
    List<UbicacionArticulo> findAllByArticuloId(Long articuloId);
    /** Bloquea y borra los vínculos del artículo; devuelve su estado bloqueado (solo escalares). */
    List<UbicacionArticulo> deleteAllByArticuloId(Long articuloId);
}
