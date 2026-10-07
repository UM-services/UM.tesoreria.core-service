package um.tesoreria.core.hexagonal.usuarios.rol.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.rol.application.exception.RolException;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in.DeleteRolUseCase;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.out.RolRepository;

@Component
@RequiredArgsConstructor
public class DeleteRolUseCaseImpl implements DeleteRolUseCase {

    private final RolRepository repository;

    @Override
    public void deleteRol(Long rolId) {
        if (rolId == null) {
            throw new RolException("rolId es requerido");
        }
        if (!repository.existsByRolId(rolId)) {
            throw new RolException("Could not find Rol with id: " + rolId);
        }
        repository.deleteByRolId(rolId);
    }
}
