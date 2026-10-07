package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.model.UsuarioPermiso;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.ports.in.DeleteUsuarioPermisoUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.ports.in.GetUsuarioPermisosByUserIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.ports.in.SetUsuarioPermisoUseCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioPermisoService {

    private final GetUsuarioPermisosByUserIdUseCase getUsuarioPermisosByUserIdUseCase;
    private final SetUsuarioPermisoUseCase setUsuarioPermisoUseCase;
    private final DeleteUsuarioPermisoUseCase deleteUsuarioPermisoUseCase;

    public List<UsuarioPermiso> findAllByUserId(Long userId) {
        return getUsuarioPermisosByUserIdUseCase.getByUserId(userId);
    }

    public UsuarioPermiso set(Long userId, Long permisoId, Byte otorgado) {
        return setUsuarioPermisoUseCase.setUsuarioPermiso(userId, permisoId, otorgado);
    }

    public void delete(Long userId, Long permisoId) {
        deleteUsuarioPermisoUseCase.deleteUsuarioPermiso(userId, permisoId);
    }
}
