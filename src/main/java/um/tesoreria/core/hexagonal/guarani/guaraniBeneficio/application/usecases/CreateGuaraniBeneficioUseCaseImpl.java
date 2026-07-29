package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.in.CreateGuaraniBeneficioUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.out.GuaraniBeneficioRepository;

@Component
@RequiredArgsConstructor
public class CreateGuaraniBeneficioUseCaseImpl implements CreateGuaraniBeneficioUseCase {
    private final GuaraniBeneficioRepository repository;

    @Override
    public GuaraniBeneficio create(GuaraniBeneficio guaraniBeneficio) {
        return repository.save(guaraniBeneficio);
    }
}
