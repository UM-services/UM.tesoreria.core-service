package um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;

import java.util.List;

public interface GetAllRolesUseCase {
    List<Rol> getAllRoles();
}
