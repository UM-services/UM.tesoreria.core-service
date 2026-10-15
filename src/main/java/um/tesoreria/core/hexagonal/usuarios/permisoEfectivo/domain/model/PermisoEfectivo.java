package um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermisoEfectivo {
    private Long userId;
    /** Claves de catálogo efectivas del usuario, ordenadas y sin repetir. */
    private List<String> permisos;
}
