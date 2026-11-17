package um.tesoreria.core.hexagonal.chequera.emision.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoEmision {

    private Long chequeraId;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer alternativaId;
    private boolean numeroNuevo;
    private boolean estabaPagada;
    private EstadoBorrador borradorEstado;
    private String auditoriaId;
    @Builder.Default
    private List<AvisoEmision> avisos = new ArrayList<>();
}
