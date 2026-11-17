package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoEmisionResponse {

    private Long chequeraId;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer alternativaId;
    private boolean numeroNuevo;
    private boolean estabaPagada;
    private String borradorEstado;
    private String auditoriaId;
    private List<BorradorResponse.AvisoDto> avisos;
}
