package um.tesoreria.core.hexagonal.usuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuario.domain.ports.in.UpdateLastLogUseCase;
import um.tesoreria.core.hexagonal.usuario.domain.ports.out.UsuarioRepository;
import um.tesoreria.core.util.Tool;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateLastLogUseCaseImpl implements UpdateLastLogUseCase {
    private final UsuarioRepository repository;

    @Transactional
    @Override
    public Optional<Usuario> updateLastLog(Long userId) {
        repository.updateLastLog(userId, Tool.hourAbsoluteArgentina());
        return repository.findByUserId(userId);
    }
}
