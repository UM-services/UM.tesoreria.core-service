package um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.dto.RolResponse;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRolResponse {
    private Long usuarioRolId;
    private Long userId;
    private Long rolId;
    private LocalDateTime created;
    private LocalDateTime updated;
    // Excepción cross-slice autorizada: expone el DTO de usuarios.rol.
    private RolResponse rol;
}
