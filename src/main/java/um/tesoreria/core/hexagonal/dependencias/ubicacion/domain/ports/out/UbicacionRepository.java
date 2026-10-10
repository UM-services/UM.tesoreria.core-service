package um.tesoreria.core.hexagonal.dependencias.ubicacion.domain.ports.out;

import um.tesoreria.core.hexagonal.dependencias.ubicacion.domain.model.Ubicacion;
import java.util.List;
import java.util.Optional;

public interface UbicacionRepository {
    List<Ubicacion> findAll();
    List<Ubicacion> findAllByGeograficaId(Integer geograficaId);
    Optional<Ubicacion> findById(Integer ubicacionId);
}
