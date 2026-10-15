package um.tesoreria.core.hexagonal.usuarios.rol.domain.model;

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
public class Rol {
    private Long rolId;
    private String nombre;
    private String descripcion;
    @Builder.Default
    private String aplicacion = "TESORERIA";
    @Builder.Default
    private Byte activo = 1;
    private LocalDateTime created;
    private LocalDateTime updated;
}
