package um.tesoreria.core.hexagonal.personas.legajo.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.legajo.domain.model.Legajo;

public interface AddLegajoUseCase {
    Legajo add(Legajo legajo);
}