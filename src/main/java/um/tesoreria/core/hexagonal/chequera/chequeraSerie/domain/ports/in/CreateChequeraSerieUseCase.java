package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;

public interface CreateChequeraSerieUseCase {
    ChequeraSerie create(ChequeraSerie chequeraSerie);
}
