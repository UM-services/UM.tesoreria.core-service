package um.tesoreria.core.hexagonal.usuarios.rolPermiso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.application.exception.RolPermisoException;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.in.DeleteRolPermisoUseCase;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.out.RolPermisoRepository;

@Component
@RequiredArgsConstructor
public class DeleteRolPermisoUseCaseImpl implements DeleteRolPermisoUseCase {

    private final RolPermisoRepository repository;

    @Override
    public void deleteRolPermiso(Long rolId, Long permisoId) {
        if (rolId == null || permisoId == null) {
            throw new RolPermisoException("rolId y permisoId son requeridos");
        }
        repository.findByRolIdAndPermisoId(rolId, permisoId)
                .orElseThrow(() -> new RolPermisoException(
                        "El rol " + rolId + " no tiene asignado el permiso " + permisoId));
        repository.deleteByRolIdAndPermisoId(rolId, permisoId);
    }
}
