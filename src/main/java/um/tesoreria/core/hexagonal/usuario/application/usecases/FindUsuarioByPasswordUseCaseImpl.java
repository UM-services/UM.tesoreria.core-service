package um.tesoreria.core.hexagonal.usuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuario.domain.ports.in.FindUsuarioByPasswordUseCase;
import um.tesoreria.core.hexagonal.usuario.domain.ports.out.UsuarioRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FindUsuarioByPasswordUseCaseImpl implements FindUsuarioByPasswordUseCase {
    private final UsuarioRepository repository;

    @Override
    public Optional<Usuario> findUsuarioByPassword(String password) {
        String encrypted = DigestUtils.sha256Hex(password);
        return repository.findByPassword(encrypted);
    }
}
