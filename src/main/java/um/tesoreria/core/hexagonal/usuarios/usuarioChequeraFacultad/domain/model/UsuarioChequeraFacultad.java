package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioChequeraFacultad {
    private Long usuarioChequeraFacultadId;
    private Long userId;
    private Integer facultadId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private Usuario usuario;
    private Facultad facultad;
}
