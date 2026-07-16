package um.tesoreria.core.hexagonal.usuario.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.usuario.application.exception.UsuarioException;
import um.tesoreria.core.hexagonal.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuario.domain.ports.in.*;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final FindUsuarioByLoginUseCase findUsuarioByLoginUseCase;
    private final FindUsuarioByPasswordUseCase findUsuarioByPasswordUseCase;
    private final CreateUsuarioUseCase createUsuarioUseCase;
    private final UpdateUsuarioUseCase updateUsuarioUseCase;
    private final UpdateLastLogUseCase updateLastLogUseCase;
    private final FindUsuarioByGoogleMailUseCase findUsuarioByGoogleMailUseCase;

    public Usuario findByLogin(String login) {
        return findUsuarioByLoginUseCase.findUsuarioByLogin(login)
                .orElseThrow(() -> new UsuarioException(login));
    }

    public Usuario findByPassword(String password) {
        return findUsuarioByPasswordUseCase.findUsuarioByPassword(password)
                .orElseThrow(UsuarioException::new);
    }

    public Usuario add(Usuario usuario) {
        return createUsuarioUseCase.createUsuario(usuario);
    }

    public Usuario update(Usuario usuario, Long userId) {
        Usuario updated = updateUsuarioUseCase.updateUsuario(usuario, userId);
        if (updated == null) {
            throw new UsuarioException(userId);
        }
        return updated;
    }

    public Usuario updateLastLog(Long userId) {
        return updateLastLogUseCase.updateLastLog(userId)
                .orElseThrow(() -> new UsuarioException(userId));
    }

    public Usuario findByGoogleMail(String googleMail) {
        return findUsuarioByGoogleMailUseCase.findUsuarioByGoogleMail(googleMail)
                .orElseThrow(() -> new UsuarioException(googleMail));
    }
}
