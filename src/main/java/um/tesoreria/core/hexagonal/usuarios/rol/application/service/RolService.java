package um.tesoreria.core.hexagonal.usuarios.rol.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.usuarios.rol.application.exception.RolException;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in.CreateRolUseCase;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in.DeleteRolUseCase;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in.GetAllRolesUseCase;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in.GetRolByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in.UpdateRolUseCase;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RolService {

    private final GetAllRolesUseCase getAllRolesUseCase;
    private final GetRolByIdUseCase getRolByIdUseCase;
    private final CreateRolUseCase createRolUseCase;
    private final UpdateRolUseCase updateRolUseCase;
    private final DeleteRolUseCase deleteRolUseCase;

    public List<Rol> findAll() {
        return getAllRolesUseCase.getAllRoles();
    }

    public Optional<Rol> findById(Long rolId) {
        return getRolByIdUseCase.getRolById(rolId);
    }

    public Rol add(Rol rol) {
        return createRolUseCase.createRol(rol);
    }

    public Rol update(Rol rol, Long rolId) {
        Rol updated = updateRolUseCase.updateRol(rol, rolId);
        if (updated == null) {
            throw new RolException(rolId);
        }
        return updated;
    }

    public void delete(Long rolId) {
        deleteRolUseCase.deleteRol(rolId);
    }
}
