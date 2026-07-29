package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.exception.GuaraniBeneficioException;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.in.CreateGuaraniBeneficioUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.in.GetGuaraniBeneficioByRequisitoUseCase;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.in.UpdateGuaraniBeneficioByRequisitoUseCase;

@Service
@RequiredArgsConstructor
public class GuaraniBeneficioService {

    private final GetGuaraniBeneficioByRequisitoUseCase getGuaraniBeneficioByRequisitoUseCase;
    private final CreateGuaraniBeneficioUseCase createGuaraniBeneficioUseCase;
    private final UpdateGuaraniBeneficioByRequisitoUseCase updateGuaraniBeneficioByRequisitoUseCase;

    public GuaraniBeneficio findByRequisito(Integer requisito) {
        return getGuaraniBeneficioByRequisitoUseCase.getGuaraniBeneficioByRequisito(requisito)
                .orElseThrow(() -> new GuaraniBeneficioException(requisito));
    }

    public GuaraniBeneficio create(GuaraniBeneficio guaraniBeneficio) {
        return createGuaraniBeneficioUseCase.create(guaraniBeneficio);
    }

    public GuaraniBeneficio updateByRequisito(Integer requisito, GuaraniBeneficio data) {
        return updateGuaraniBeneficioByRequisitoUseCase.updateByRequisito(requisito, data);
    }
}
