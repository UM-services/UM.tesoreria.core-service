package um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
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
public class RolRequest {

    @NotBlank
    private String nombre;

    private String descripcion;

    private String aplicacion;

    private Byte activo;
}
