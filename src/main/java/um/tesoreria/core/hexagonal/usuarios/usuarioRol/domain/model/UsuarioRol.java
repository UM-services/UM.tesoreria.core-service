package um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRol {
    private Long usuarioRolId;
    private Long userId;
    private Long rolId;
    private LocalDateTime created;
    private LocalDateTime updated;
    // Excepción cross-slice autorizada: anclaje al catálogo usuarios.rol
    // (mismo precedente que usuarioChequeraGeografica ancla dependencias.geografica).
    private Rol rol;
}
