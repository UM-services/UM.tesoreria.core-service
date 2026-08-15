package um.tesoreria.core.hexagonal.dependencias.ubicacion.domain.ports.in;

import um.tesoreria.core.hexagonal.dependencias.ubicacion.domain.model.Ubicacion;
import java.util.List;

public interface GetUbicacionesBySedeUseCase {
    List<Ubicacion> getUbicacionesBySede(Integer geograficaId);
}
