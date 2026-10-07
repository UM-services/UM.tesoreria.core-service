package um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;

public interface CreateRolUseCase {
    Rol createRol(Rol rol);
}
