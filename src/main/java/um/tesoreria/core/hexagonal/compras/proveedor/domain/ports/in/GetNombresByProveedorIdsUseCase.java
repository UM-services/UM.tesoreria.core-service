package um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.ProveedorNombre;

import java.util.Collection;
import java.util.List;

public interface GetNombresByProveedorIdsUseCase {

    /** Razón social de los proveedores pedidos, en una sola consulta. Un id que no existe no aparece. */
    List<ProveedorNombre> getNombresByProveedorIds(Collection<Integer> proveedorIds);
}
