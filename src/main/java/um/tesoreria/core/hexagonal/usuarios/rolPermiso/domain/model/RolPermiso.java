package um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolPermiso {
    private Long rolPermisoId;
    private Long rolId;
    private Long permisoId;
    private LocalDateTime created;
    private LocalDateTime updated;
    // Excepción cross-slice autorizada: anclaje a los catálogos usuarios.rol y usuarios.permiso
    // (mismo precedente que usuarioChequeraGeografica ancla dependencias.geografica).
    private Rol rol;
    private Permiso permiso;
}
