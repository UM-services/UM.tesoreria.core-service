package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.in;

import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;
import java.util.Optional;

public interface GetGuaraniBeneficioByRequisitoUseCase {
    Optional<GuaraniBeneficio> getGuaraniBeneficioByRequisito(Integer requisito);
}
