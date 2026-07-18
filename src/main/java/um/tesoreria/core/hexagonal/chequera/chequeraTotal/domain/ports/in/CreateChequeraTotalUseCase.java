package um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;

import java.util.List;

public interface CreateChequeraTotalUseCase {
    List<ChequeraTotal> createAll(List<ChequeraTotal> chequeraTotals);
}
