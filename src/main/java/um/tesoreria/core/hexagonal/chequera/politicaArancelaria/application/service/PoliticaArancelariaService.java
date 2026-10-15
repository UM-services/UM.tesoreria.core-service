package um.tesoreria.core.hexagonal.chequera.politicaArancelaria.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.politicaArancelaria.domain.ports.in.RecalculateCuotaByUniqueIndexUseCase;

@Service
@RequiredArgsConstructor
public class PoliticaArancelariaService {

    private final RecalculateCuotaByUniqueIndexUseCase findPoliticaArancelariaUseCase;

    /**
     * Recalcula la cuota saneando previamente los importes (arancel parcial). El endpoint ahora <b>escribe</b>:
     * persiste los importes saneados de la cuota en revisión y de la referencia, dentro de una transacción.
     */
    @Transactional
    public ChequeraCuota recalculateCuotaByUniqueIndex(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                              Integer productoId, Integer alternativaId, Integer cuotaId, Integer plazo) {
        return findPoliticaArancelariaUseCase.recalculateCuota(facultadId, tipoChequeraId, chequeraSerieId,
                productoId, alternativaId, cuotaId, plazo);
    }
}
