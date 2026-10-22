package um.tesoreria.core.hexagonal.usuarios.usuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuario.application.exception.LoginNoDisponibleException;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.LoginGenerator;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in.CreateUsuarioUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.out.UsuarioRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateUsuarioUseCaseImpl implements CreateUsuarioUseCase {

    private final UsuarioRepository repository;

    @Override
    public Usuario createUsuario(Usuario usuario) {
        String apellido = usuario.getApellido();
        boolean tieneApellido = apellido != null && !apellido.isBlank();

        // Los logins se generan sólo cuando el alta no trae uno explícito.
        List<String> candidatos = null;
        if (usuario.getLogin() == null || usuario.getLogin().isBlank()) {
            if (!tieneApellido) {
                throw new IllegalArgumentException("El apellido es obligatorio para generar el login");
            }
            candidatos = LoginGenerator.candidatos(usuario.getNombre(), apellido);
        }

        usuario.setPassword(DigestUtils.sha256Hex(usuario.getPassword()));
        if (tieneApellido) {
            usuario.setNombre(usuario.getNombre().trim() + " " + apellido.trim());
        }

        // Compatibilidad: un login explícito (API/legacy) se respeta tal cual.
        if (candidatos == null) {
            try {
                return repository.save(usuario);
            } catch (DataIntegrityViolationException e) {
                throw new LoginNoDisponibleException(
                        "El login '" + usuario.getLogin() + "' ya está en uso");
            }
        }

        for (String candidato : candidatos) {
            if (repository.existsByLoginIgnoreCase(candidato)) {
                continue;
            }
            // Un intento fallido puede dejar el id asignado en memoria (IDENTITY);
            // se limpia para que el siguiente candidato se inserte y no se actualice.
            usuario.setUserId(null);
            usuario.setLogin(candidato);
            try {
                return repository.save(usuario);
            } catch (DataIntegrityViolationException e) {
                // Carrera: otro alta tomó el login entre la verificación y el insert.
            }
        }

        throw new LoginNoDisponibleException(
                "No se pudo asignar un login único para " + usuario.getNombre());
    }
}
