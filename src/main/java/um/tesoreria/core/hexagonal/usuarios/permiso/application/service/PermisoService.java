package um.tesoreria.core.hexagonal.usuarios.permiso.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.exception.PermisoException;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.CreatePermisoUseCase;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.DeletePermisoUseCase;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.GetAllPermisosUseCase;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.GetPermisoByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.UpdatePermisoUseCase;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PermisoService {

    private final GetAllPermisosUseCase getAllPermisosUseCase;
    private final GetPermisoByIdUseCase getPermisoByIdUseCase;
    private final CreatePermisoUseCase createPermisoUseCase;
    private final UpdatePermisoUseCase updatePermisoUseCase;
    private final DeletePermisoUseCase deletePermisoUseCase;

    public List<Permiso> findAll() {
        return getAllPermisosUseCase.getAllPermisos();
    }

    public Optional<Permiso> findById(Long permisoId) {
        return getPermisoByIdUseCase.getPermisoById(permisoId);
    }

    public Permiso add(Permiso permiso) {
        return createPermisoUseCase.createPermiso(permiso);
    }

    public Permiso update(Permiso permiso, Long permisoId) {
        Permiso updated = updatePermisoUseCase.updatePermiso(permiso, permisoId);
        if (updated == null) {
            throw new PermisoException(permisoId);
        }
        return updated;
    }

    public void delete(Long permisoId) {
        deletePermisoUseCase.deletePermiso(permisoId);
    }
}
