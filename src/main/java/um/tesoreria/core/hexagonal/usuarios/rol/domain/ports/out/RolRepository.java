package um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.out;

import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;

import java.util.List;
import java.util.Optional;

public interface RolRepository {
    Optional<Rol> findByRolId(Long rolId);

    List<Rol> findAll();

    Rol save(Rol rol);

    void deleteByRolId(Long rolId);

    boolean existsByRolId(Long rolId);
}
