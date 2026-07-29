package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.in.GetGuaraniBeneficioByRequisitoUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.out.GuaraniBeneficioRepository;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetGuaraniBeneficioByRequisitoUseCaseImpl implements GetGuaraniBeneficioByRequisitoUseCase {
    private final GuaraniBeneficioRepository repository;

    @Override
    public Optional<GuaraniBeneficio> getGuaraniBeneficioByRequisito(Integer requisito) {
        return repository.findByRequisito(requisito);
    }
}
