package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.FindPagosByChequeraUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindPagosByChequeraUseCaseImpl implements FindPagosByChequeraUseCase {

    private final ChequeraPagoRepository repository;

    @Override
    public List<ChequeraPago> findPagosByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        return repository.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId);
    }

}
