package um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;
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
public class UsuarioRolRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long rolId;
}
