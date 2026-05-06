package um.tesoreria.core.hexagonal.ubicacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ubicacion {
    private Integer ubicacionId;
    private String nombre;
    private Integer dependenciaId;
    private Integer geograficaId; // Derived from Dependencia if needed
}
