package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto.PermisoResponse;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPermisoResponse {
    private Long usuarioPermisoId;
    private Long userId;
    private Long permisoId;
    private Byte otorgado;
    private LocalDateTime created;
    private LocalDateTime updated;
    // Excepción cross-slice autorizada: expone el DTO de usuarios.permiso.
    private PermisoResponse permiso;
}
