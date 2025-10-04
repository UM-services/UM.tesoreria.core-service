package um.tesoreria.core.hexagonal.cursoCargoContratado.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DesignacionTipo {

    private Integer designacionTipoId;
    private String nombre;
    private BigDecimal horasSemanales;
    private BigDecimal horasTotales;
    private Integer simples;

}
