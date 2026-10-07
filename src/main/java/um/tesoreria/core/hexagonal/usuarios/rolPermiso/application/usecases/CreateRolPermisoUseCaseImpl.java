package um.tesoreria.core.hexagonal.usuarios.rolPermiso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.GetPermisoByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in.GetRolByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.application.exception.RolPermisoException;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.in.CreateRolPermisoUseCase;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.out.RolPermisoRepository;

@Component
@RequiredArgsConstructor
public class CreateRolPermisoUseCaseImpl implements CreateRolPermisoUseCase {

    private final RolPermisoRepository repository;
    // Excepción cross-slice autorizada: valida las referencias consumiendo los puertos
    // públicos de usuarios.rol y usuarios.permiso.
    private final GetRolByIdUseCase getRolByIdUseCase;
    private final GetPermisoByIdUseCase getPermisoByIdUseCase;

    @Override
    public RolPermiso createRolPermiso(RolPermiso rolPermiso) {
        if (rolPermiso == null) {
            throw new RolPermisoException("La asignación es requerida");
        }
        Long rolId = rolPermiso.getRolId();
        Long permisoId = rolPermiso.getPermisoId();
        if (rolId == null) {
            throw new RolPermisoException("rolId es requerido");
        }
        if (permisoId == null) {
            throw new RolPermisoException("permisoId es requerido");
        }
        getRolByIdUseCase.getRolById(rolId)
                .orElseThrow(() -> new RolPermisoException("Cannot find Rol with id: " + rolId));
        getPermisoByIdUseCase.getPermisoById(permisoId)
                .orElseThrow(() -> new RolPermisoException("Cannot find Permiso with id: " + permisoId));
        // Idempotente: si el permiso ya está asignado al rol, se devuelve la asignación existente.
        return repository.findByRolIdAndPermisoId(rolId, permisoId)
                .orElseGet(() -> repository.save(RolPermiso.builder()
                        .rolId(rolId)
                        .permisoId(permisoId)
                        .build()));
    }
}
