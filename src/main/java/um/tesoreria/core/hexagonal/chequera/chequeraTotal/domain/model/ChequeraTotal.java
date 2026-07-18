package um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChequeraTotal {

    private Long chequeraTotalId;
    private Integer facultadId;
    private Integer tipoChequeraId;
    private Long chequeraSerieId;
    private Integer productoId;
    private BigDecimal total;
    private BigDecimal pagado;

}
