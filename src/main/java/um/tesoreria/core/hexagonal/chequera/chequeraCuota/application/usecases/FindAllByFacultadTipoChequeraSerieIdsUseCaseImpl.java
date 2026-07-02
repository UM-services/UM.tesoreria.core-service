package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.FindAllByFacultadTipoChequeraSerieIdsUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllByFacultadTipoChequeraSerieIdsUseCaseImpl implements FindAllByFacultadTipoChequeraSerieIdsUseCase {

    private final ChequeraCuotaRepository repository;

    @Override
    public List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIds(Integer facultadId, Integer tipoChequeraId, List<Long> chequeraSerieIds) {
        return repository.findAllByFacultadTipoChequeraSerieIds(facultadId, tipoChequeraId, chequeraSerieIds);
    }
}
