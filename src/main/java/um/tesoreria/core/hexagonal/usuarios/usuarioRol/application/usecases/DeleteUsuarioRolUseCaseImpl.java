package um.tesoreria.core.hexagonal.usuarios.usuarioRol.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.application.exception.UsuarioRolException;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.ports.in.DeleteUsuarioRolUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.ports.out.UsuarioRolRepository;

@Component
@RequiredArgsConstructor
public class DeleteUsuarioRolUseCaseImpl implements DeleteUsuarioRolUseCase {

    private final UsuarioRolRepository repository;

    @Override
    public void deleteUsuarioRol(Long userId, Long rolId) {
        if (userId == null || rolId == null) {
            throw new UsuarioRolException("userId y rolId son requeridos");
        }
        repository.findByUserIdAndRolId(userId, rolId)
                .orElseThrow(() -> new UsuarioRolException(
                        "El usuario " + userId + " no tiene asignado el rol " + rolId));
        repository.deleteByUserIdAndRolId(userId, rolId);
    }
}
