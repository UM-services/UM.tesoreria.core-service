package um.tesoreria.core.hexagonal.usuarios.usuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in.ResetUsuarioPasswordUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.out.UsuarioRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ResetUsuarioPasswordUseCaseImpl implements ResetUsuarioPasswordUseCase {

    private final UsuarioRepository repository;

    @Override
    public Optional<Usuario> resetPassword(Long userId, String newPassword) {
        return repository.findByUserId(userId).map(usuario -> {
            usuario.setPassword(DigestUtils.sha256Hex(newPassword));
            // El admin fija una clave provisoria: se fuerza el cambio al próximo ingreso.
            usuario.setDebeCambiarClave((byte) 1);
            return repository.save(usuario);
        });
    }
}
