package um.tesoreria.core.hexagonal.chequeraCuota.domain.ports.in;

import um.tesoreria.core.hexagonal.chequeraCuota.domain.model.ChequeraSerie;
import um.tesoreria.core.model.dto.DeudaChequeraDto;

public interface CalculateDeudaUseCase {

    DeudaChequeraDto calculateDeuda(ChequeraSerie chequeraSerie);

    DeudaChequeraDto calculateDeudaExtended(ChequeraSerie chequeraSerie);

}
