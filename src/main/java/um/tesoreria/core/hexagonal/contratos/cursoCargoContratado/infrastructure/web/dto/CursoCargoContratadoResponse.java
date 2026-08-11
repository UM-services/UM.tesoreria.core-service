package um.tesoreria.core.hexagonal.contratos.cursoCargoContratado.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.contratos.contrato.domain.model.Contrato;
import um.tesoreria.core.hexagonal.contratos.cursoCargoContratado.domain.model.CargoTipo;
import um.tesoreria.core.hexagonal.contratos.cursoCargoContratado.domain.model.CursoHaberes;
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
