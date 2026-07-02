package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in;

import um.tesoreria.core.model.view.ChequeraKey;
import java.util.List;

public interface GetChequeraSerieByCbuOrVisaUseCase {
    List<ChequeraKey> getAllByCbu(String cbu, Integer debitoTipoId);
    List<ChequeraKey> getAllByVisa(String numeroTarjeta, Integer debitoTipoId);
}
