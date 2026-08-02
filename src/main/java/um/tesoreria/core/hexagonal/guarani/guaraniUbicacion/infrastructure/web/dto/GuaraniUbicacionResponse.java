package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.infrastructure.web.dto;

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
public class GuaraniUbicacionResponse {
    private Integer guaraniUbicacionId;
    private Integer ubicacion;
    private Integer geograficaId;
}
