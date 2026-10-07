package um.tesoreria.core.hexagonal.usuarios.rolPermiso.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.in.CreateRolPermisoUseCase;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.in.DeleteRolPermisoUseCase;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.in.GetRolPermisosByRolIdUseCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolPermisoService {

    private final GetRolPermisosByRolIdUseCase getRolPermisosByRolIdUseCase;
    private final CreateRolPermisoUseCase createRolPermisoUseCase;
    private final DeleteRolPermisoUseCase deleteRolPermisoUseCase;

    public List<RolPermiso> findAllByRolId(Long rolId) {
        return getRolPermisosByRolIdUseCase.getByRolId(rolId);
    }

    public RolPermiso add(RolPermiso rolPermiso) {
        return createRolPermisoUseCase.createRolPermiso(rolPermiso);
    }

    public void delete(Long rolId, Long permisoId) {
        deleteRolPermisoUseCase.deleteRolPermiso(rolId, permisoId);
    }
}
