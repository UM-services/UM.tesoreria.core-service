package um.tesoreria.core.hexagonal.usuarios.permiso.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Resultado de reconciliar el catálogo contra el manifiesto `permisos.yaml`.
 * Los conteos y listas describen exactamente lo que se hizo (o se haría, en dry-run).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncPermisosResultado {

    private boolean dryRun;
    private int totalManifiesto;
    private int agregados;
    private int actualizados;
    private int retirados;
    private int sinCambios;
    private List<String> clavesAgregadas;
    private List<String> clavesActualizadas;
    private List<String> clavesRetiradas;
}
