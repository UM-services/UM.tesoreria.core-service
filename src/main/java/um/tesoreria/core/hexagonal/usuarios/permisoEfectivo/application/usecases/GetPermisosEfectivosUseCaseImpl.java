package um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.application.exception.PermisoEfectivoException;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.model.PermisoEfectivo;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.ports.in.GetPermisosEfectivosUseCase;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.in.GetRolPermisosByRolIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in.GetUsuarioByIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.model.UsuarioPermiso;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.ports.in.GetUsuarioPermisosByUserIdUseCase;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.model.UsuarioRol;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.ports.in.GetUsuarioRolesByUserIdUseCase;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Component
@RequiredArgsConstructor
public class GetPermisosEfectivosUseCaseImpl implements GetPermisosEfectivosUseCase {

    // Excepción cross-slice autorizada: este slice compone la lectura de los slices de unión
    // usuarios.usuarioRol / usuarios.rolPermiso / usuarios.usuarioPermiso y del catálogo
    // usuarios.usuario, consumiendo solo sus puertos públicos (inbound ports).
    private final GetUsuarioRolesByUserIdUseCase getUsuarioRolesByUserIdUseCase;
    private final GetRolPermisosByRolIdUseCase getRolPermisosByRolIdUseCase;
    private final GetUsuarioPermisosByUserIdUseCase getUsuarioPermisosByUserIdUseCase;
    private final GetUsuarioByIdUseCase getUsuarioByIdUseCase;

    @Override
    public PermisoEfectivo getPermisosEfectivos(Long userId) {
        if (userId == null) {
            throw new PermisoEfectivoException("userId es requerido");
        }
        Usuario usuario = getUsuarioByIdUseCase.getUsuarioById(userId)
                .orElseThrow(() -> new PermisoEfectivoException("Cannot find Usuario with id: " + userId));

        Set<String> claves = new TreeSet<>();

        // 1) Permisos heredados de los roles asignados al usuario.
        List<UsuarioRol> roles = getUsuarioRolesByUserIdUseCase.getByUserId(userId);
        for (UsuarioRol usuarioRol : roles) {
            for (RolPermiso rolPermiso : getRolPermisosByRolIdUseCase.getByRolId(usuarioRol.getRolId())) {
                Permiso permiso = rolPermiso.getPermiso();
                if (isActivo(permiso)) {
                    claves.add(permiso.getClave());
                }
            }
        }

        // 2) Overrides individuales: 1 otorga, 0 revoca (pisa lo heredado del rol).
        for (UsuarioPermiso override : getUsuarioPermisosByUserIdUseCase.getByUserId(userId)) {
            Permiso permiso = override.getPermiso();
            if (permiso == null || permiso.getClave() == null) {
                continue;
            }
            if (override.getOtorgado() != null && override.getOtorgado() == 1) {
                if (isActivo(permiso)) {
                    claves.add(permiso.getClave());
                }
            } else {
                claves.remove(permiso.getClave());
            }
        }

        // 3) Puente de compatibilidad con los flags legacy de usuario (no se migran datos).
        addLegacy(claves, "administrador.panel", usuario.getAdministrador());
        addLegacy(claves, "chequeras.imprimir", usuario.getImprimeChequera());
        addLegacy(claves, "chequeras.numero_manual", usuario.getNumeroOpManual());
        addLegacy(claves, "chequeras.habilitar_eliminacion", usuario.getHabilitaOpEliminacion());
        addLegacy(claves, "chequeras.eliminar", usuario.getEliminaChequera());
        addLegacy(claves, "chequeras.modificar", usuario.getModificaChequera());

        return PermisoEfectivo.builder()
                .userId(userId)
                .permisos(List.copyOf(claves))
                .build();
    }

    private boolean isActivo(Permiso permiso) {
        return permiso != null && permiso.getClave() != null
                && permiso.getActivo() != null && permiso.getActivo() == 1;
    }

    private void addLegacy(Set<String> claves, String clave, Byte flag) {
        if (flag != null && flag == 1) {
            claves.add(clave);
        }
    }
}
