package um.tesoreria.core.hexagonal.usuarios.usuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in.GetUsuarioByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.out.UsuarioRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetUsuarioByIdUseCaseImpl implements GetUsuarioByIdUseCase {

    private final UsuarioRepository usuarioRepository;

    @Override
    public Optional<Usuario> getUsuarioById(Long userId) {
        return usuarioRepository.findByUserId(userId);
    }
}
