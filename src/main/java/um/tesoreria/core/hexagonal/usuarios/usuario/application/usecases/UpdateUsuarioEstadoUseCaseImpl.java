package um.tesoreria.core.hexagonal.usuarios.usuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in.UpdateUsuarioEstadoUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.out.UsuarioRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateUsuarioEstadoUseCaseImpl implements UpdateUsuarioEstadoUseCase {

    private final UsuarioRepository repository;

    @Override
    public Optional<Usuario> updateEstado(Long userId, Byte activo) {
        return repository.findByUserId(userId).map(usuario -> {
            usuario.setActivo(activo);
            return repository.save(usuario);
        });
    }
}
