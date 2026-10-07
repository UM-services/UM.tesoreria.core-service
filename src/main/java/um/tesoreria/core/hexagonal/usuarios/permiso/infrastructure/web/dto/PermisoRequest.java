package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto;

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
public class PermisoRequest {

    @NotBlank
    private String clave;

    @NotBlank
    private String descripcion;

    @NotBlank
    private String modulo;

    private String aplicacion;

    private Byte activo;
}
