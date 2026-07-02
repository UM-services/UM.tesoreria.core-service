package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.FindAllPendientesUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindAllPendientesUseCaseImpl implements FindAllPendientesUseCase {

    private final ChequeraCuotaRepository repository;

    @Override
    public List<ChequeraCuota> findAllPendientes(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        log.debug("\n\nProcessing ChequeraCuotaService.findAllPendientes\n\n");
        return repository.findAllPendientes(facultadId, tipoChequeraId, chequeraSerieId, alternativaId);
    }
}
