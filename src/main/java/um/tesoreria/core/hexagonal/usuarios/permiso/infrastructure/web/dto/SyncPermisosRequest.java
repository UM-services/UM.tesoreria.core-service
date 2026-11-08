package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncPermisosRequest {

    /** true = sólo calcula el diff; false/ausente = aplica los cambios. */
    private Boolean dryRun;
}
