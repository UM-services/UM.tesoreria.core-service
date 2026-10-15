package um.tesoreria.core.hexagonal.usuarios.rol.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.rol.application.exception.RolException;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in.CreateRolUseCase;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.out.RolRepository;

@Component
@RequiredArgsConstructor
public class CreateRolUseCaseImpl implements CreateRolUseCase {

    private final RolRepository repository;

    @Override
    public Rol createRol(Rol rol) {
        if (rol == null || rol.getNombre() == null || rol.getNombre().isBlank()) {
            throw new RolException("El nombre del rol es requerido");
        }
        return repository.save(rol);
    }
}
