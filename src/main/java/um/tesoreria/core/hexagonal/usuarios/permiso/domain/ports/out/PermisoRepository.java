package um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.out;

import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;

import java.util.List;
import java.util.Optional;

public interface PermisoRepository {
    Optional<Permiso> findByPermisoId(Long permisoId);

    Optional<Permiso> findByAplicacionAndClave(String aplicacion, String clave);

    List<Permiso> findAll();

    Permiso save(Permiso permiso);

    void deleteByPermisoId(Long permisoId);

    boolean existsByPermisoId(Long permisoId);
}
