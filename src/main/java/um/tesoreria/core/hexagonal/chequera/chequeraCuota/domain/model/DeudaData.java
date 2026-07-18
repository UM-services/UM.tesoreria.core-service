package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeudaData {

    private List<ChequeraCuota> cuotas;
    private List<ChequeraPago> pagos;
    private List<ChequeraTotal> totals;

}
