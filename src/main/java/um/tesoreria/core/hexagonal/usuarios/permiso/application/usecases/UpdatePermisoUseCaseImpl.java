package um.tesoreria.core.hexagonal.usuarios.permiso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.exception.PermisoException;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.UpdatePermisoUseCase;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.out.PermisoRepository;

@Component
@RequiredArgsConstructor
public class UpdatePermisoUseCaseImpl implements UpdatePermisoUseCase {

    private final PermisoRepository repository;

    @Override
    public Permiso updatePermiso(Permiso newPermiso, Long permisoId) {
        if (newPermiso == null) {
            throw new PermisoException("El permiso es requerido");
        }
        return repository.findByPermisoId(permisoId).map(permiso -> {
            String clave = newPermiso.getClave() != null ? newPermiso.getClave() : permiso.getClave();
            String aplicacion = newPermiso.getAplicacion() != null
                    ? newPermiso.getAplicacion()
                    : permiso.getAplicacion();
            repository.findByAplicacionAndClave(aplicacion, clave)
                    .filter(otro -> !otro.getPermisoId().equals(permisoId))
                    .ifPresent(otro -> {
                        throw new PermisoException(
                                "Ya existe el permiso " + clave + " para " + aplicacion);
                    });
            permiso.setClave(clave);
            permiso.setDescripcion(newPermiso.getDescripcion());
            permiso.setModulo(newPermiso.getModulo());
            permiso.setAplicacion(aplicacion);
            if (newPermiso.getActivo() != null) {
                permiso.setActivo(newPermiso.getActivo());
            }
            return repository.save(permiso);
        }).orElse(null);
    }
}
