package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneracionJobTarea {

    private Long tareaId;
    private Long jobId;
    private Long chequeraId;
    private BigDecimal personaId;
    private Integer documentoId;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer cursoId;
    private String personaNombre;
    private Long borradorId;
    private GeneracionTareaEstado estado;
    private String codigo;
    private String mensaje;
    private OffsetDateTime finalizado;
}
