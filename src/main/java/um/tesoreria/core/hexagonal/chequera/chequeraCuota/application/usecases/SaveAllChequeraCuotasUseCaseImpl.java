package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.SaveAllChequeraCuotasUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;

import jakarta.transaction.Transactional;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SaveAllChequeraCuotasUseCaseImpl implements SaveAllChequeraCuotasUseCase {

    private final ChequeraCuotaRepository repository;

    @Override
    @Transactional
    public List<ChequeraCuota> saveAll(List<ChequeraCuota> chequeraCuotas) {
        return repository.saveAll(chequeraCuotas);
    }
}
