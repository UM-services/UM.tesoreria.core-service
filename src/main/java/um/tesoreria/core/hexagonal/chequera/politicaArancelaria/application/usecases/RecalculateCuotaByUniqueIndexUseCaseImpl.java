package um.tesoreria.core.hexagonal.chequera.politicaArancelaria.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.politicaArancelaria.domain.ports.in.RecalculateCuotaByUniqueIndexUseCase;

@Component
@RequiredArgsConstructor
public class RecalculateCuotaByUniqueIndexUseCaseImpl implements RecalculateCuotaByUniqueIndexUseCase {

    private final ChequeraCuotaService chequeraCuotaService;

    @Override
    public ChequeraCuota recalculateCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId) {
        var cuota = chequeraCuotaService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId);
        return cuota;
    }

}
