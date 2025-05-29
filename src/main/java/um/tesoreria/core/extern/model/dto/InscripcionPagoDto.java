package um.tesoreria.core.extern.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InscripcionPagoDto {

    private Long inscripcionPagoId;
    private BigDecimal personaId;
    private Integer documentoId;
    private Integer facultadId;
    private Integer lectivoId;
    private BigDecimal personaIdPagador;
    private Integer documentoIdPagador;

}
