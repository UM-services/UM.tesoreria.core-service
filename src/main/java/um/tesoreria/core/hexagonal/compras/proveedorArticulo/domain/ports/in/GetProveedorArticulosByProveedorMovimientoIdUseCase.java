package um.tesoreria.core.hexagonal.compras.proveedorArticulo.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.proveedorArticulo.domain.model.ProveedorArticulo;

import java.util.List;

public interface GetProveedorArticulosByProveedorMovimientoIdUseCase {

    List<ProveedorArticulo> getProveedorArticulos(Long proveedorMovimientoId);

    List<ProveedorArticulo> getProveedorArticulos(List<Long> proveedorMovimientoIds);
}
