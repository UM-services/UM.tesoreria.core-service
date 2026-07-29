package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.exception.GuaraniBeneficioException;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.in.UpdateGuaraniBeneficioByRequisitoUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.out.GuaraniBeneficioRepository;

@Component
@RequiredArgsConstructor
public class UpdateGuaraniBeneficioByRequisitoUseCaseImpl implements UpdateGuaraniBeneficioByRequisitoUseCase {
    private final GuaraniBeneficioRepository repository;

    @Override
    public GuaraniBeneficio updateByRequisito(Integer requisito, GuaraniBeneficio data) {
        GuaraniBeneficio existing = repository.findByRequisito(requisito)
                .orElseThrow(() -> new GuaraniBeneficioException(requisito));
        existing.setPorcentajeBeneficio(data.getPorcentajeBeneficio());
        return repository.save(existing);
    }
}
