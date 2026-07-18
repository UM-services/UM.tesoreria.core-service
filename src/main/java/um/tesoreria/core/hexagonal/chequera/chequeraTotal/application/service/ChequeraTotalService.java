package um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.exception.ChequeraTotalException;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.in.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChequeraTotalService {

    private final FindAllTotalByChequeraUseCase findAllByChequeraUseCase;
    private final FindByUniqueChequeraTotalUseCase findByUniqueChequeraTotalUseCase;
    private final CreateChequeraTotalUseCase createChequeraTotalUseCase;
    private final DeleteChequeraTotalUseCase deleteChequeraTotalUseCase;
    private final UpdateChequeraTotalUseCase updateChequeraTotalUseCase;

    public List<ChequeraTotal> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        return findAllByChequeraUseCase.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId);
    }

    public ChequeraTotal findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId) {
        return findByUniqueChequeraTotalUseCase.findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId)
                .orElseThrow(() -> new ChequeraTotalException(facultadId, tipoChequeraId, chequeraSerieId, productoId));
    }

    public List<ChequeraTotal> saveAll(List<ChequeraTotal> chequeraTotals) {
        return createChequeraTotalUseCase.createAll(chequeraTotals);
    }

    public void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        deleteChequeraTotalUseCase.deleteAllByChequera(facultadId, tipoChequeraId, chequeraSerieId);
    }

    public ChequeraTotal update(ChequeraTotal chequeraTotal, Long chequeraTotalId) {
        return updateChequeraTotalUseCase.update(chequeraTotal, chequeraTotalId);
    }
}
