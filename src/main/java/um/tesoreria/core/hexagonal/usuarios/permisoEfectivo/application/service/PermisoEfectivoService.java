package um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.model.PermisoEfectivo;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.ports.in.GetPermisosEfectivosUseCase;

@Service
@RequiredArgsConstructor
public class PermisoEfectivoService {

    private final GetPermisosEfectivosUseCase getPermisosEfectivosUseCase;

    public PermisoEfectivo getPermisosEfectivos(Long userId) {
        return getPermisosEfectivosUseCase.getPermisosEfectivos(userId);
    }
}
