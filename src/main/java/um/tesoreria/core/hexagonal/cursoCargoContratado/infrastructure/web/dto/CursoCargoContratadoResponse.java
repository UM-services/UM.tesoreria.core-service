package um.tesoreria.core.hexagonal.cursoCargoContratado.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.cursoCargoContratado.domain.model.CargoTipo;
import um.tesoreria.core.hexagonal.cursoCargoContratado.domain.model.Categoria;
import um.tesoreria.core.hexagonal.cursoCargoContratado.domain.model.CursoHaberes;
import um.tesoreria.core.hexagonal.cursoCargoContratado.domain.model.DesignacionTipo;
import um.tesoreria.core.model.Contrato;
import um.tesoreria.core.util.Jsonifier;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoCargoContratadoResponse {

    private Long cursoCargoContratadoId;
    private CursoHaberes curso;
    private Integer anho;
    private Integer mes;
    private Contrato contrato;
    private CargoTipo cargoTipo;
    private BigDecimal horasSemanales;
    private BigDecimal horasTotales;
    private Byte acreditado;

    public String jsonify() {
        return Jsonifier.builder(this).build();
    }

}
