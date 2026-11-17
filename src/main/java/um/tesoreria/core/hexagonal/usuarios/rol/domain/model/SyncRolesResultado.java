package um.tesoreria.core.hexagonal.usuarios.rol.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Resultado de reconciliar los roles contra el manifiesto `roles.yaml`.
 * Los conteos y listas describen exactamente lo que se hizo (o se haría, en dry-run).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncRolesResultado {

    private boolean dryRun;
    private int totalManifiesto;
    private int rolesAgregados;
    private int rolesActualizados;
    private int rolesSinCambios;
    private int permisosAgregados;
    private int permisosQuitados;
    private List<String> rolesCreados;
    private List<String> rolesModificados;
    private List<String> asignacionesAgregadas;
    private List<String> asignacionesQuitadas;
}
