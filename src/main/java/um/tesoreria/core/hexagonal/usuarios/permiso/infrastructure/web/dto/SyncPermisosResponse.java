package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncPermisosResponse {

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
