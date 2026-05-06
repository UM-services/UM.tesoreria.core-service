package um.tesoreria.core.hexagonal.ubicacion.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionResponse {
    private Integer ubicacionId;
    private String nombre;
    private Integer dependenciaId;
    private Integer geograficaId;
}
