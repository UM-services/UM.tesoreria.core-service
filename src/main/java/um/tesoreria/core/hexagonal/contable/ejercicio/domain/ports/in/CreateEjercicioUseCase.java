package um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;

public interface CreateEjercicioUseCase {
    Ejercicio createEjercicio(Ejercicio ejercicio);
}
