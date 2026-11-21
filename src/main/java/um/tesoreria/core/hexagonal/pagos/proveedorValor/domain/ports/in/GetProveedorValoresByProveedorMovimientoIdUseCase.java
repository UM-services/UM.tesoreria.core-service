package um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.model.ProveedorValor;

import java.util.List;

public interface GetProveedorValoresByProveedorMovimientoIdUseCase {

    List<ProveedorValor> getProveedorValores(Long proveedorMovimientoId);
}
