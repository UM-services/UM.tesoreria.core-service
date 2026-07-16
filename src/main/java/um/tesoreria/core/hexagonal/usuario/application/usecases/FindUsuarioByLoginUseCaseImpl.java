package um.tesoreria.core.hexagonal.usuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuario.domain.ports.in.FindUsuarioByLoginUseCase;
import um.tesoreria.core.hexagonal.usuario.domain.ports.out.UsuarioRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FindUsuarioByLoginUseCaseImpl implements FindUsuarioByLoginUseCase {
    private final UsuarioRepository repository;

    @Override
    public Optional<Usuario> findUsuarioByLogin(String login) {
        return repository.findByLogin(login);
    }
}
