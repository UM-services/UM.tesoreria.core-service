package um.tesoreria.core.hexagonal.usuarios.usuarioRol.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.model.UsuarioRol;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.ports.in.GetUsuarioRolesByUserIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.ports.out.UsuarioRolRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetUsuarioRolesByUserIdUseCaseImpl implements GetUsuarioRolesByUserIdUseCase {

    private final UsuarioRolRepository repository;

    @Override
    public List<UsuarioRol> getByUserId(Long userId) {
        return repository.findAllByUserId(userId);
    }
}
