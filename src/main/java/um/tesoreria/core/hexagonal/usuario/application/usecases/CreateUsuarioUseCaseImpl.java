package um.tesoreria.core.hexagonal.usuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuario.domain.ports.in.CreateUsuarioUseCase;
import um.tesoreria.core.hexagonal.usuario.domain.ports.out.UsuarioRepository;

@Component
@RequiredArgsConstructor
public class CreateUsuarioUseCaseImpl implements CreateUsuarioUseCase {
    private final UsuarioRepository repository;

    @Override
    public Usuario createUsuario(Usuario usuario) {
        usuario.setPassword(DigestUtils.sha256Hex(usuario.getPassword()));
        return repository.save(usuario);
    }
}
