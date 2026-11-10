package um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.SyncPermisosResultado;

import java.util.List;

public interface SyncPermisosUseCase {

    /**
     * Reconcilia el catálogo contra el manifiesto.
     *
     * @param manifiesto permisos declarados en el archivo (identidad: aplicacion + clave)
     * @param dryRun     true = calcula el diff sin escribir
     */
    SyncPermisosResultado sync(List<Permiso> manifiesto, boolean dryRun);
}
