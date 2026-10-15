package um.tesoreria.core.hexagonal.usuarios.permiso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.exception.PermisoException;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.DeletePermisoUseCase;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.out.PermisoRepository;

@Component
@RequiredArgsConstructor
public class DeletePermisoUseCaseImpl implements DeletePermisoUseCase {

    private final PermisoRepository repository;

    @Override
    public void deletePermiso(Long permisoId) {
        if (permisoId == null) {
            throw new PermisoException("permisoId es requerido");
        }
        if (!repository.existsByPermisoId(permisoId)) {
            throw new PermisoException("Could not find Permiso with id: " + permisoId);
        }
        repository.deleteByPermisoId(permisoId);
    }
}
