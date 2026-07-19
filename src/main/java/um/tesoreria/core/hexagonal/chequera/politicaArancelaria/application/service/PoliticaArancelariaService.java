package um.tesoreria.core.hexagonal.chequera.politicaArancelaria.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.politicaArancelaria.domain.ports.in.RecalculateCuotaByUniqueIndexUseCase;

@Service
@RequiredArgsConstructor
public class PoliticaArancelariaService {

    private final RecalculateCuotaByUniqueIndexUseCase findPoliticaArancelariaUseCase;

    public ChequeraCuota recalculateCuotaByUniqueIndex(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                              Integer productoId, Integer alternativaId, Integer cuotaId, Integer plazo) {
        return findPoliticaArancelariaUseCase.recalculateCuota(facultadId, tipoChequeraId, chequeraSerieId,
                productoId, alternativaId, cuotaId, plazo);
    }
}
