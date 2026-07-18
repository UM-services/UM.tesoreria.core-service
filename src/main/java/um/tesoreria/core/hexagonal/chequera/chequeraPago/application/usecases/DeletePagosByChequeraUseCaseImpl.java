package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.DeletePagosByChequeraUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;

@Component
@RequiredArgsConstructor
public class DeletePagosByChequeraUseCaseImpl implements DeletePagosByChequeraUseCase {

    private final ChequeraPagoRepository repository;

    @Override
    public void deletePagosByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        repository.deleteAllByChequera(facultadId, tipoChequeraId, chequeraSerieId);
    }

}
