package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.out;

import java.util.List;
import java.util.Optional;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.model.GuaraniUbicacion;

public interface GuaraniUbicacionRepository {
    List<GuaraniUbicacion> findAll();

    Optional<GuaraniUbicacion> findById(Integer id);

    Optional<GuaraniUbicacion> findByUbicacion(Integer ubicacion);

    GuaraniUbicacion save(GuaraniUbicacion guaraniUbicacion);

    void deleteById(Integer id);
}
