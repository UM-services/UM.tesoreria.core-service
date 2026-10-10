package um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/** Reset de clave por administración: clave nueva en texto plano y su confirmación. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPasswordRequest {

    @NotBlank(message = "La clave nueva es obligatoria")
    private String password;

    private String reClave;

}
