package um.tesoreria.core.hexagonal.compras.proveedorArticulo.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.proveedorArticulo.domain.model.ProveedorArticulo;

import java.util.List;

public interface ProveedorArticuloRepository {

    List<ProveedorArticulo> findAllByProveedorMovimientoId(Long proveedorMovimientoId);

    List<ProveedorArticulo> findAllByProveedorMovimientoIdIn(List<Long> proveedorMovimientoIds);
}
