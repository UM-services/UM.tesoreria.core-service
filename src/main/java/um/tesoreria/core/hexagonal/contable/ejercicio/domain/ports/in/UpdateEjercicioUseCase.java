package um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;

public interface UpdateEjercicioUseCase {
    Ejercicio updateEjercicio(Integer ejercicioId, Ejercicio cambios);
}
