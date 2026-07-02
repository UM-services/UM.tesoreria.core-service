package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.GetChequeraSerieByNumberUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out.ChequeraSerieRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetChequeraSerieByNumberUseCaseImpl implements GetChequeraSerieByNumberUseCase {

    private final ChequeraSerieRepository repository;

    @Override
    public List<ChequeraSerie> getAllByNumber(Integer facultadId, Long chequeraSerieId) {
        return repository.findAllByFacultadIdAndChequeraSerieId(facultadId, chequeraSerieId, Sort.by("lectivoId").descending());
    }
}
