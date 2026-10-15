package um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.web.dto;

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
public class RolPermisoRequest {

    @NotNull
    private Long rolId;

    @NotNull
    private Long permisoId;
}
