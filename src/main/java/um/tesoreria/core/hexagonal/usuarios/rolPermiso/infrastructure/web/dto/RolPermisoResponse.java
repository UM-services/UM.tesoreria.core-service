package um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto.PermisoResponse;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.dto.RolResponse;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolPermisoResponse {
    private Long rolPermisoId;
    private Long rolId;
    private Long permisoId;
    private LocalDateTime created;
    private LocalDateTime updated;
    // Excepción cross-slice autorizada: expone los DTO de usuarios.rol y usuarios.permiso.
    private RolResponse rol;
    private PermisoResponse permiso;
}
