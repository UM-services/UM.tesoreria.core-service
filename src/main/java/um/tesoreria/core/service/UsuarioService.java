/**
 *
 */
package um.tesoreria.core.service;

import um.tesoreria.core.kotlin.model.Usuario;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.UsuarioException;
import um.tesoreria.core.repository.UsuarioRepository;
import um.tesoreria.core.util.Tool;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    public Usuario findByLogin(String login) {
        return repository.findByLogin(login).orElseThrow(() -> new UsuarioException(login));
    }

    public Usuario findByPassword(String password) {
        String encrypted = DigestUtils.sha256Hex(password);
        return repository.findByPassword(encrypted)
                .orElseThrow(UsuarioException::new);
    }

    public Usuario add(Usuario usuario) {
        usuario.setPassword(DigestUtils.sha256Hex(usuario.getPassword()));
        usuario = repository.save(usuario);
        return usuario;
    }

    public Usuario update(Usuario newUsuario, Long userId) {
        return repository.findByUserId(userId).map(usuario -> {
            usuario = new Usuario(userId,
                    newUsuario.getLogin(),
                    DigestUtils.sha256Hex(newUsuario.getPassword()),
                    newUsuario.getNombre(),
                    newUsuario.getGeograficaId(),
                    Objects.requireNonNullElse(newUsuario.getImprimeChequera(), (byte) 0),
                    Objects.requireNonNullElse(newUsuario.getNumeroOpManual(), (byte) 0),
                    Objects.requireNonNullElse(newUsuario.getHabilitaOpEliminacion(), (byte) 0),
                    Objects.requireNonNullElse(newUsuario.getEliminaChequera(), (byte) 0),
                    Tool.hourAbsoluteArgentina(),
                    newUsuario.getGoogleMail(),
                    Objects.requireNonNullElse(newUsuario.getActivo(), (byte) 1));
            usuario = repository.save(usuario);
            return usuario;
        }).orElseThrow(() -> new UsuarioException(userId));
    }

    @org.springframework.transaction.annotation.Transactional
    public Usuario updateLastLog(Long userId) {
        repository.updateLastLog(userId, Tool.hourAbsoluteArgentina());
        return repository.findByUserId(userId).orElseThrow(() -> new UsuarioException(userId));
    }

    public Usuario findByGoogleMail(String googleMail) {
        return repository.findByGoogleMailAndActivo(googleMail, (byte) 1)
                .orElseThrow(() -> new UsuarioException(googleMail));
    }

}
