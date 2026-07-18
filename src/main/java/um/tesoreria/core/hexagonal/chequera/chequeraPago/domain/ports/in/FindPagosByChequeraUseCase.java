package um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;

import java.util.List;

public interface FindPagosByChequeraUseCase {
    List<ChequeraPago> findPagosByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);
}
