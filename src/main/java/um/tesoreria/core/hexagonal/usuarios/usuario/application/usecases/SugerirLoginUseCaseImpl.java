package um.tesoreria.core.hexagonal.usuarios.usuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuario.application.exception.LoginNoDisponibleException;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.LoginGenerator;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in.SugerirLoginUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.out.UsuarioRepository;

@Component
@RequiredArgsConstructor
public class SugerirLoginUseCaseImpl implements SugerirLoginUseCase {

    private final UsuarioRepository repository;

    @Override
    public String sugerirLogin(String nombre, String apellido) {
        return LoginGenerator.candidatos(nombre, apellido).stream()
                .filter(candidato -> !repository.existsByLoginIgnoreCase(candidato))
                .findFirst()
                .orElseThrow(() -> new LoginNoDisponibleException(
                        "No se pudo generar un login disponible para " + nombre + " " + apellido));
    }
}
