package um.tesoreria.core.hexagonal.usuarios.rol.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Rol declarado en el manifiesto `roles.yaml`.
 *
 * <p>Es la semilla versionada de un rol y de su matriz `rol_permiso`. En
 * `permisos` se aceptan claves exactas y comodines (`*`, `modulo.*`).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolSemilla {

    private String nombre;
    private String descripcion;
    @Builder.Default
    private String aplicacion = "TESORERIA";
    @Builder.Default
    private Byte activo = 1;
    @Builder.Default
    private List<String> permisos = List.of();
}
