package um.tesoreria.core.hexagonal.usuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuario.domain.ports.in.FindUsuarioByGoogleMailUseCase;
import um.tesoreria.core.hexagonal.usuario.domain.ports.out.UsuarioRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FindUsuarioByGoogleMailUseCaseImpl implements FindUsuarioByGoogleMailUseCase {
    private final UsuarioRepository repository;

    @Override
    public Optional<Usuario> findUsuarioByGoogleMail(String googleMail) {
        return repository.findByGoogleMailAndActivo(googleMail, (byte) 1);
    }
}
