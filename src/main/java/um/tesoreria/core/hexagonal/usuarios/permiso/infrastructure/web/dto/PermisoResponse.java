package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto;

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
public class PermisoResponse {
    private Long permisoId;
    private String clave;
    private String descripcion;
    private String modulo;
    private String aplicacion;
    private Byte activo;
    private LocalDateTime created;
    private LocalDateTime updated;
}
