package um.tesoreria.core.hexagonal.usuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuario.domain.ports.in.UpdateUsuarioUseCase;
import um.tesoreria.core.hexagonal.usuario.domain.ports.out.UsuarioRepository;
import um.tesoreria.core.util.Tool;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UpdateUsuarioUseCaseImpl implements UpdateUsuarioUseCase {
    private final UsuarioRepository repository;

    @Override
    public Usuario updateUsuario(Usuario newUsuario, Long userId) {
        return repository.findByUserId(userId).map(usuario -> {
            usuario.setLogin(newUsuario.getLogin());
            usuario.setPassword(DigestUtils.sha256Hex(newUsuario.getPassword()));
            usuario.setNombre(newUsuario.getNombre());
            usuario.setGeograficaId(newUsuario.getGeograficaId());
            usuario.setImprimeChequera(Objects.requireNonNullElse(newUsuario.getImprimeChequera(), (byte) 0));
            usuario.setNumeroOpManual(Objects.requireNonNullElse(newUsuario.getNumeroOpManual(), (byte) 0));
            usuario.setHabilitaOpEliminacion(Objects.requireNonNullElse(newUsuario.getHabilitaOpEliminacion(), (byte) 0));
            usuario.setEliminaChequera(Objects.requireNonNullElse(newUsuario.getEliminaChequera(), (byte) 0));
            usuario.setModificaChequera(newUsuario.getModificaChequera());
            usuario.setLastLog(Tool.hourAbsoluteArgentina());
            usuario.setGoogleMail(newUsuario.getGoogleMail());
            usuario.setActivo(Objects.requireNonNullElse(newUsuario.getActivo(), (byte) 1));
            return repository.save(usuario);
        }).orElse(null);
    }
}
