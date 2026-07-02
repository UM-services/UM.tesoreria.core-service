package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.model.dto.DeudaChequeraDto;

public interface CalculateDeudaUseCase {

    DeudaChequeraDto calculateDeuda(ChequeraSerie chequeraSerie);

    DeudaChequeraDto calculateDeudaExtended(ChequeraSerie chequeraSerie);

}
