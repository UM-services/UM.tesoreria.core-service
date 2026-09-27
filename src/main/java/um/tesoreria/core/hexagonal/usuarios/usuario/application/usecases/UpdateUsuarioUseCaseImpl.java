package um.tesoreria.core.hexagonal.usuarios.usuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in.UpdateUsuarioUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.out.UsuarioRepository;
import um.tesoreria.core.util.Tool;

@Component
@RequiredArgsConstructor
public class UpdateUsuarioUseCaseImpl implements UpdateUsuarioUseCase {
    private final UsuarioRepository repository;

    @Override
    public Usuario updateUsuario(Usuario newUsuario, Long userId) {
        return repository.findByUserId(userId).map(usuario -> {
            // PUT estricto: los flags obligatorios se validan en la frontera (UsuarioRequest @NotNull).
            // Si un flag llega null (llamada interna sin validacion), updateEntity lo ignora
            // y el valor existente queda intacto: nunca se pisa implicitamente.
            usuario.setLogin(newUsuario.getLogin());
            usuario.setPassword(DigestUtils.sha256Hex(newUsuario.getPassword()));
            usuario.setNombre(newUsuario.getNombre());
            usuario.setGeograficaId(newUsuario.getGeograficaId());
            usuario.setImprimeChequera(newUsuario.getImprimeChequera());
            usuario.setNumeroOpManual(newUsuario.getNumeroOpManual());
            usuario.setHabilitaOpEliminacion(newUsuario.getHabilitaOpEliminacion());
            usuario.setEliminaChequera(newUsuario.getEliminaChequera());
            usuario.setModificaChequera(newUsuario.getModificaChequera());
            usuario.setLastLog(Tool.hourAbsoluteArgentina());
            usuario.setGoogleMail(newUsuario.getGoogleMail());
            usuario.setActivo(newUsuario.getActivo());
            return repository.save(usuario);
        }).orElse(null);
    }
}
