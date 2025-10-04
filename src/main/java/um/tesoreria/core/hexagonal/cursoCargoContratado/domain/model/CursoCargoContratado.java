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
public class CursoCargoContratado {

    private Long cursoCargoContratadoId = null;
    private Long cursoId = null;
    private Integer anho = 0;
    private Integer mes = 0;
    private Long contratoId = null;
    private BigDecimal personaId = null;
    private Integer documentoId = null;
    private Integer cargoTipoId = null;
    private BigDecimal horasSemanales = BigDecimal.ZERO;
    private BigDecimal horasTotales = BigDecimal.ZERO;
    private Integer designacionTipoId = null;
    private Integer categoriaId = null;
    private Long cursoCargoNovedadId = null;
    private Byte acreditado = 0;

}