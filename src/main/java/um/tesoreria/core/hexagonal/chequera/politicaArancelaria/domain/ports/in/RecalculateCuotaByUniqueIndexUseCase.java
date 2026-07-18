package um.tesoreria.core.hexagonal.chequera.politicaArancelaria.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;

public interface RecalculateCuotaByUniqueIndexUseCase {
    ChequeraCuota recalculateCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                       Integer productoId, Integer alternativaId, Integer cuotaId);
}
