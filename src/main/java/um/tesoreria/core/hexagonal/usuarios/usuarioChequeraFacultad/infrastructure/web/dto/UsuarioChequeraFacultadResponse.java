package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.dependencias.facultad.infrastructure.web.dto.FacultadResponse;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.dto.UsuarioResponse;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioChequeraFacultadResponse {
    private Long usuarioChequeraFacultadId;
    private Long userId;
    private Integer facultadId;
    private LocalDateTime created;
    private LocalDateTime updated;
    private UsuarioResponse usuario;
    private FacultadResponse facultad;
}
