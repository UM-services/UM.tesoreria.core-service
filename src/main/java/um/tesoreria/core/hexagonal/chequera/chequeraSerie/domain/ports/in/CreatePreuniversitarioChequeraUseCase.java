package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.PreuniversitarioChequeraData;

public interface CreatePreuniversitarioChequeraUseCase {
    ChequeraSerie create(PreuniversitarioChequeraData data);
}
