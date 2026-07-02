package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.DeleteAllByChequeraUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;

import jakarta.transaction.Transactional;

@Component
@RequiredArgsConstructor
public class DeleteAllByChequeraUseCaseImpl implements DeleteAllByChequeraUseCase {

    private final ChequeraCuotaRepository repository;

    @Override
    @Transactional
    public void deleteAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        repository.deleteAllByChequera(facultadId, tipoChequeraId, chequeraSerieId);
    }
}
