package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPermiso {
    private Long usuarioPermisoId;
    private Long userId;
    private Long permisoId;
    /** 1 = otorgado aunque ningún rol lo traiga; 0 = revocado aunque un rol lo traiga. */
    private Byte otorgado;
    private LocalDateTime created;
    private LocalDateTime updated;
    // Excepción cross-slice autorizada: anclaje al catálogo usuarios.permiso
    // (mismo precedente que usuarioChequeraGeografica ancla dependencias.geografica).
    private Permiso permiso;
}
