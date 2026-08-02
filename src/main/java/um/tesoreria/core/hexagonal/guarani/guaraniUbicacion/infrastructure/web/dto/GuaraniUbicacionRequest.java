package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GuaraniUbicacionRequest {
    private Integer ubicacion;
    private Integer geograficaId;
}
