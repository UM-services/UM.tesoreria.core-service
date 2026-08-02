package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.in.GetAllGuaraniBeneficiosUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.out.GuaraniBeneficioRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllGuaraniBeneficiosUseCaseImpl implements GetAllGuaraniBeneficiosUseCase {

    private final GuaraniBeneficioRepository guaraniBeneficioRepository;

    @Override
    public List<GuaraniBeneficio> getAllGuaraniBeneficios() {
        return guaraniBeneficioRepository.findAll();
    }
}
