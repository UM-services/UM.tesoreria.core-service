package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.web.dto;

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
public class UsuarioPermisoRequest {

    /** 1 = otorgado aunque ningún rol lo traiga; 0 = revocado aunque un rol lo traiga. */
    @NotNull
    private Byte otorgado;
}
