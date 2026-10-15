package um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in;

import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;

import java.util.List;

/**
 * Borra todos los vínculos de un artículo dentro de la transacción de su baja y devuelve cómo estaban
 * (solo datos escalares, sin asociaciones).
 */
public interface DeleteUbicacionArticulosByArticuloUseCase {
    List<UbicacionArticulo> deleteByArticuloId(Long articuloId);
}
