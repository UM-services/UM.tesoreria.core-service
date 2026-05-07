package um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in;

import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;

public interface SaveUbicacionArticuloUseCase {
    UbicacionArticulo save(UbicacionArticulo ubicacionArticulo);
}
