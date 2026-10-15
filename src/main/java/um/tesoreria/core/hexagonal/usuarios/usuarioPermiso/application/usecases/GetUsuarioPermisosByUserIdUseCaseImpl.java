package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.model.UsuarioPermiso;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.ports.in.GetUsuarioPermisosByUserIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.ports.out.UsuarioPermisoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetUsuarioPermisosByUserIdUseCaseImpl implements GetUsuarioPermisosByUserIdUseCase {

    private final UsuarioPermisoRepository repository;

    @Override
    public List<UsuarioPermiso> getByUserId(Long userId) {
        return repository.findAllByUserId(userId);
    }
}
