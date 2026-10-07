package um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;

import java.util.Optional;

public interface GetRolByIdUseCase {
    Optional<Rol> getRolById(Long rolId);
}
