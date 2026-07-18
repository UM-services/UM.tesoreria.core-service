package um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;

import java.util.List;

public interface FindPagosByCuotaUseCase {
    List<ChequeraPago> findPagosByCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                         Integer productoId, Integer alternativaId, Integer cuotaId);
}
