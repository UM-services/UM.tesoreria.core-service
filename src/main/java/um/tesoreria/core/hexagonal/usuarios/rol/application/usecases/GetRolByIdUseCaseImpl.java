package um.tesoreria.core.hexagonal.usuarios.rol.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in.GetRolByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.out.RolRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetRolByIdUseCaseImpl implements GetRolByIdUseCase {

    private final RolRepository repository;

    @Override
    public Optional<Rol> getRolById(Long rolId) {
        return repository.findByRolId(rolId);
    }
}
