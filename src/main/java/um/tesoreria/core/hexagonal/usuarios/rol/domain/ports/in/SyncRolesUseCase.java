package um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.RolSemilla;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.SyncRolesResultado;

import java.util.List;

public interface SyncRolesUseCase {

    /**
     * Reconcilia roles y matrices `rol_permiso` contra el manifiesto.
     *
     * @param manifiesto roles declarados en el archivo (identidad: aplicacion + nombre)
     * @param dryRun     true = calcula el diff sin escribir
     */
    SyncRolesResultado sync(List<RolSemilla> manifiesto, boolean dryRun);
}
