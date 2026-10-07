package um.tesoreria.core.hexagonal.usuarios.usuarioRol.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in.GetRolByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in.GetUsuarioByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.application.exception.UsuarioRolException;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.model.UsuarioRol;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.ports.in.CreateUsuarioRolUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.ports.out.UsuarioRolRepository;

@Component
@RequiredArgsConstructor
public class CreateUsuarioRolUseCaseImpl implements CreateUsuarioRolUseCase {

    private final UsuarioRolRepository repository;
    // Excepción cross-slice autorizada: valida las referencias consumiendo los puertos
    // públicos de usuarios.usuario y usuarios.rol.
    private final GetUsuarioByIdUseCase getUsuarioByIdUseCase;
    private final GetRolByIdUseCase getRolByIdUseCase;

    @Override
    public UsuarioRol createUsuarioRol(UsuarioRol usuarioRol) {
        if (usuarioRol == null) {
            throw new UsuarioRolException("La asignación es requerida");
        }
        Long userId = usuarioRol.getUserId();
        Long rolId = usuarioRol.getRolId();
        if (userId == null) {
            throw new UsuarioRolException("userId es requerido");
        }
        if (rolId == null) {
            throw new UsuarioRolException("rolId es requerido");
        }
        getUsuarioByIdUseCase.getUsuarioById(userId)
                .orElseThrow(() -> new UsuarioRolException("Cannot find Usuario with id: " + userId));
        getRolByIdUseCase.getRolById(rolId)
                .orElseThrow(() -> new UsuarioRolException("Cannot find Rol with id: " + rolId));
        // Idempotente: si el rol ya está asignado al usuario, se devuelve la asignación existente.
        return repository.findByUserIdAndRolId(userId, rolId)
                .orElseGet(() -> repository.save(UsuarioRol.builder()
                        .userId(userId)
                        .rolId(rolId)
                        .build()));
    }
}
