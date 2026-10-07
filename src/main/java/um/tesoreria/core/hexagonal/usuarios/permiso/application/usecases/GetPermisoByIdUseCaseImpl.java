package um.tesoreria.core.hexagonal.usuarios.permiso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.GetPermisoByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.out.PermisoRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetPermisoByIdUseCaseImpl implements GetPermisoByIdUseCase {

    private final PermisoRepository repository;

    @Override
    public Optional<Permiso> getPermisoById(Long permisoId) {
        return repository.findByPermisoId(permisoId);
    }
}
