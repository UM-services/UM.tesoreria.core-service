package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.application.exception.UsuarioPermisoException;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.ports.in.DeleteUsuarioPermisoUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.ports.out.UsuarioPermisoRepository;

@Component
@RequiredArgsConstructor
public class DeleteUsuarioPermisoUseCaseImpl implements DeleteUsuarioPermisoUseCase {

    private final UsuarioPermisoRepository repository;

    @Override
    public void deleteUsuarioPermiso(Long userId, Long permisoId) {
        if (userId == null || permisoId == null) {
            throw new UsuarioPermisoException("userId y permisoId son requeridos");
        }
        repository.findByUserIdAndPermisoId(userId, permisoId)
                .orElseThrow(() -> new UsuarioPermisoException(
                        "El usuario " + userId + " no tiene override para el permiso " + permisoId));
        repository.deleteByUserIdAndPermisoId(userId, permisoId);
    }
}
