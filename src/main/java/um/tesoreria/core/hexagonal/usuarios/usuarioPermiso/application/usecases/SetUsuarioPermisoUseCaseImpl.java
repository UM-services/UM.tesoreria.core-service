package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.GetPermisoByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in.GetUsuarioByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.application.exception.UsuarioPermisoException;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.model.UsuarioPermiso;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.ports.in.SetUsuarioPermisoUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.ports.out.UsuarioPermisoRepository;

@Component
@RequiredArgsConstructor
public class SetUsuarioPermisoUseCaseImpl implements SetUsuarioPermisoUseCase {

    private final UsuarioPermisoRepository repository;
    // Excepción cross-slice autorizada: valida las referencias consumiendo los puertos
    // públicos de usuarios.usuario y usuarios.permiso.
    private final GetUsuarioByIdUseCase getUsuarioByIdUseCase;
    private final GetPermisoByIdUseCase getPermisoByIdUseCase;

    @Override
    public UsuarioPermiso setUsuarioPermiso(Long userId, Long permisoId, Byte otorgado) {
        if (userId == null) {
            throw new UsuarioPermisoException("userId es requerido");
        }
        if (permisoId == null) {
            throw new UsuarioPermisoException("permisoId es requerido");
        }
        if (otorgado == null || (otorgado != 0 && otorgado != 1)) {
            throw new UsuarioPermisoException("otorgado debe ser 1 (otorgado) o 0 (revocado)");
        }
        getUsuarioByIdUseCase.getUsuarioById(userId)
                .orElseThrow(() -> new UsuarioPermisoException("Cannot find Usuario with id: " + userId));
        getPermisoByIdUseCase.getPermisoById(permisoId)
                .orElseThrow(() -> new UsuarioPermisoException("Cannot find Permiso with id: " + permisoId));
        // Upsert: si ya existe el override se actualiza el flag; si no, se crea.
        UsuarioPermiso override = repository.findByUserIdAndPermisoId(userId, permisoId)
                .orElseGet(() -> UsuarioPermiso.builder()
                        .userId(userId)
                        .permisoId(permisoId)
                        .build());
        override.setOtorgado(otorgado);
        return repository.save(override);
    }
}
