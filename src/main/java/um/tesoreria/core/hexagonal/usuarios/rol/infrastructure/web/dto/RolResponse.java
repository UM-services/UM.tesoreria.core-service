package um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolResponse {
    private Long rolId;
    private String nombre;
    private String descripcion;
    private String aplicacion;
    private Byte activo;
    private LocalDateTime created;
    private LocalDateTime updated;
}
