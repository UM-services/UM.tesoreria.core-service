package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.in.GetGuaraniBeneficiosByRequisitosUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.out.GuaraniBeneficioRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetGuaraniBeneficiosByRequisitosUseCaseImpl implements GetGuaraniBeneficiosByRequisitosUseCase {
    private final GuaraniBeneficioRepository repository;

    @Override
    public List<GuaraniBeneficio> getGuaraniBeneficiosByRequisitos(List<Integer> requisitos) {
        return repository.findByRequisitos(requisitos);
    }
}
