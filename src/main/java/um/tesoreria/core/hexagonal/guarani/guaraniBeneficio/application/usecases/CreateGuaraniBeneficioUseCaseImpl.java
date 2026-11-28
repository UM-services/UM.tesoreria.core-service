package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.exception.GuaraniBeneficioException;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.in.CreateGuaraniBeneficioUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.out.GuaraniBeneficioRepository;

@Component
@RequiredArgsConstructor
public class CreateGuaraniBeneficioUseCaseImpl implements CreateGuaraniBeneficioUseCase {
    private final GuaraniBeneficioRepository repository;

    @Override
    public GuaraniBeneficio create(GuaraniBeneficio guaraniBeneficio) {
        if (repository.findByRequisito(guaraniBeneficio.getRequisito()).isPresent()) {
            throw new GuaraniBeneficioException("Ya existe un beneficio para el requisito: "
                    + guaraniBeneficio.getRequisito());
        }
        try {
            return repository.save(guaraniBeneficio);
        } catch (DataIntegrityViolationException exception) {
            throw new GuaraniBeneficioException("Ya existe un beneficio para el requisito: "
                    + guaraniBeneficio.getRequisito());
        }
    }
}
