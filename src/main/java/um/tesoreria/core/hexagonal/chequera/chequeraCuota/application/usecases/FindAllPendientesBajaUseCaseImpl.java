package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.FindAllPendientesBajaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllPendientesBajaUseCaseImpl implements FindAllPendientesBajaUseCase {

    private final ChequeraCuotaRepository repository;

    @Override
    public List<ChequeraCuota> findAllPendientesBaja(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        return repository.findAllPendientesBaja(facultadId, tipoChequeraId, chequeraSerieId, alternativaId);
    }
}
