package um.tesoreria.core.hexagonal.umhub.consulta.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.DeudaChequeraDto;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.VencimientoDto;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaDeudaResponse {
    private String numeroDocumento;
    private Integer cuotas;
    private BigDecimal deuda;
    private List<DeudaChequeraDto> deudas;
    private List<VencimientoDto> vencimientos;
}
