package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.ports.in;

import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.model.GuaraniUbicacion;

public interface CreateGuaraniUbicacionUseCase {
    GuaraniUbicacion create(GuaraniUbicacion guaraniUbicacion);
}
