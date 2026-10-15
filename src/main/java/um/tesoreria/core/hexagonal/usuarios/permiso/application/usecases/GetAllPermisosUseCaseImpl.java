package um.tesoreria.core.hexagonal.usuarios.permiso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.GetAllPermisosUseCase;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.out.PermisoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllPermisosUseCaseImpl implements GetAllPermisosUseCase {

    private final PermisoRepository repository;

    @Override
    public List<Permiso> getAllPermisos() {
        return repository.findAll();
    }
}
