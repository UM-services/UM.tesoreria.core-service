package um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.ports.out;

import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.model.ProveedorValor;

import java.util.List;

public interface ProveedorValorRepository {

    List<ProveedorValor> findAllByProveedorMovimientoId(Long proveedorMovimientoId);

    ProveedorValor save(ProveedorValor proveedorValor);
}
