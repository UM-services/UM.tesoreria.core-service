package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.FindAllPeriodosLectivoUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;
import um.tesoreria.core.model.internal.CuotaPeriodoDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllPeriodosLectivoUseCaseImpl implements FindAllPeriodosLectivoUseCase {

    private final ChequeraCuotaRepository repository;

    @Override
    public List<CuotaPeriodoDto> findAllPeriodosLectivo(Integer lectivoId) {
        return repository.findCuotaPeriodosByLectivoId(lectivoId);
    }
}
