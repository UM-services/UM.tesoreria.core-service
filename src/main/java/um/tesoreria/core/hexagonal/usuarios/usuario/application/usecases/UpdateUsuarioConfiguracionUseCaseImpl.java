package um.tesoreria.core.hexagonal.usuarios.usuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in.UpdateUsuarioConfiguracionUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.out.UsuarioRepository;

import java.util.Optional;

/**
 * Reemplazo total de los campos de configuración (datos y flags) sobre el usuario
 * existente. Nunca toca login ni clave: se trabaja sobre la entidad ya cargada, así
 * que el valor persistido de esos dos campos queda intacto.
 */
@Component
@RequiredArgsConstructor
public class UpdateUsuarioConfiguracionUseCaseImpl implements UpdateUsuarioConfiguracionUseCase {

    private final UsuarioRepository repository;

    @Override
    public Optional<Usuario> updateConfiguracion(Usuario cambios, Long userId) {
        return repository.findByUserId(userId).map(usuario -> {
            usuario.setNombre(cambios.getNombre());
            usuario.setDependenciaId(cambios.getDependenciaId());
            usuario.setGeograficaId(cambios.getGeograficaId());
            usuario.setGoogleMail(cambios.getGoogleMail());
            usuario.setImprimeChequera(cambios.getImprimeChequera());
            usuario.setNumeroOpManual(cambios.getNumeroOpManual());
            usuario.setHabilitaOpEliminacion(cambios.getHabilitaOpEliminacion());
            usuario.setEliminaChequera(cambios.getEliminaChequera());
            usuario.setModificaChequera(cambios.getModificaChequera());
            usuario.setActivo(cambios.getActivo());
            usuario.setAdministrador(cambios.getAdministrador());
            usuario.setUsuarioExterno(cambios.getUsuarioExterno());
            return repository.save(usuario);
        });
    }
}
