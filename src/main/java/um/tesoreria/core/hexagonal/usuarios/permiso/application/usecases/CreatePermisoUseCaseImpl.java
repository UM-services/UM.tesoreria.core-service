package um.tesoreria.core.hexagonal.usuarios.permiso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.exception.PermisoException;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.CreatePermisoUseCase;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.out.PermisoRepository;

@Component
@RequiredArgsConstructor
public class CreatePermisoUseCaseImpl implements CreatePermisoUseCase {

    private final PermisoRepository repository;

    @Override
    public Permiso createPermiso(Permiso permiso) {
        if (permiso == null || permiso.getClave() == null || permiso.getClave().isBlank()) {
            throw new PermisoException("La clave del permiso es requerida");
        }
        if (permiso.getDescripcion() == null || permiso.getDescripcion().isBlank()) {
            throw new PermisoException("La descripción del permiso es requerida");
        }
        if (permiso.getModulo() == null || permiso.getModulo().isBlank()) {
            throw new PermisoException("El módulo del permiso es requerido");
        }
        String aplicacion = permiso.getAplicacion() != null ? permiso.getAplicacion() : "TESORERIA";
        if (repository.findByAplicacionAndClave(aplicacion, permiso.getClave()).isPresent()) {
            throw new PermisoException("Ya existe el permiso " + permiso.getClave() + " para " + aplicacion);
        }
        return repository.save(permiso);
    }
}
