package um.tesoreria.core.hexagonal.usuarios.permiso.domain.model;

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
public class Permiso {
    private Long permisoId;
    private String clave;
    private String descripcion;
    private String modulo;
    @Builder.Default
    private String aplicacion = "TESORERIA";
    @Builder.Default
    private Byte activo = 1;
    private LocalDateTime created;
    private LocalDateTime updated;
}
