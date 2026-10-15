package um.tesoreria.core.hexagonal.usuarios.rol.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.rol.application.exception.RolException;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in.UpdateRolUseCase;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.out.RolRepository;

@Component
@RequiredArgsConstructor
public class UpdateRolUseCaseImpl implements UpdateRolUseCase {

    private final RolRepository repository;

    @Override
    public Rol updateRol(Rol newRol, Long rolId) {
        if (newRol == null) {
            throw new RolException("El rol es requerido");
        }
        return repository.findByRolId(rolId).map(rol -> {
            rol.setNombre(newRol.getNombre());
            rol.setDescripcion(newRol.getDescripcion());
            if (newRol.getAplicacion() != null) rol.setAplicacion(newRol.getAplicacion());
            if (newRol.getActivo() != null) rol.setActivo(newRol.getActivo());
            return repository.save(rol);
        }).orElse(null);
    }
}
