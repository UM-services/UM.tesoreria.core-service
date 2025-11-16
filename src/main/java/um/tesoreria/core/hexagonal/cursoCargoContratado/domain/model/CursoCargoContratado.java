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

    private Long cursoCargoContratadoId;
    private Long cursoId;

    @Builder.Default
    private Integer anho = 0;
    @Builder.Default
    private Integer mes = 0;
    private Long contratoId;
    private BigDecimal personaId;
    private Integer documentoId;
    private Integer cargoTipoId;
    @Builder.Default
    private BigDecimal horasSemanales = BigDecimal.ZERO;
    @Builder.Default
    private BigDecimal horasTotales = BigDecimal.ZERO;
    private Integer designacionTipoId;
    private Integer categoriaId;
    private Long cursoCargoNovedadId;
    @Builder.Default
    private Byte acreditado = 0;

}