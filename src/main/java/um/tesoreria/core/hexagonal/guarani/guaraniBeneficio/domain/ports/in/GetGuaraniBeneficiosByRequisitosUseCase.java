package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.ports.in;

import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;

import java.util.List;

public interface GetGuaraniBeneficiosByRequisitosUseCase {
    List<GuaraniBeneficio> getGuaraniBeneficiosByRequisitos(List<Integer> requisitos);
}
