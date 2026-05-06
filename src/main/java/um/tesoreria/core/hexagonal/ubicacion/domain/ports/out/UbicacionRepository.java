package um.tesoreria.core.hexagonal.ubicacion.domain.ports.out;

import um.tesoreria.core.hexagonal.ubicacion.domain.model.Ubicacion;
import java.util.List;

public interface UbicacionRepository {
    List<Ubicacion> findAll();
    List<Ubicacion> findAllByGeograficaId(Integer geograficaId);
}
