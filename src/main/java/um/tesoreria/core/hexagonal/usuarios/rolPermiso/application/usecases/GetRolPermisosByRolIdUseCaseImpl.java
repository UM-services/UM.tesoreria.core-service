package um.tesoreria.core.hexagonal.usuarios.rolPermiso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.in.GetRolPermisosByRolIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.out.RolPermisoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetRolPermisosByRolIdUseCaseImpl implements GetRolPermisosByRolIdUseCase {

    private final RolPermisoRepository repository;

    @Override
    public List<RolPermiso> getByRolId(Long rolId) {
        return repository.findAllByRolId(rolId);
    }
}
