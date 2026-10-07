package um.tesoreria.core.hexagonal.usuarios.rol.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in.GetAllRolesUseCase;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.out.RolRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllRolesUseCaseImpl implements GetAllRolesUseCase {

    private final RolRepository repository;

    @Override
    public List<Rol> getAllRoles() {
        return repository.findAll();
    }
}
