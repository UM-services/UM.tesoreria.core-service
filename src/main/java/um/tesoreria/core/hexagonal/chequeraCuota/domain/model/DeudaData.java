package um.tesoreria.core.hexagonal.chequeraCuota.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
