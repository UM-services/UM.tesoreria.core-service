package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuaraniUbicacion {
    private Integer guaraniUbicacionId;
    private Integer ubicacion;
    private Integer geograficaId;
}
