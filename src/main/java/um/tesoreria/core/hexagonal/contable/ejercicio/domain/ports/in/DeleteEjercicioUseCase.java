package um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;

public interface DeleteEjercicioUseCase {
    void deleteEjercicio(Integer ejercicioId);
}
