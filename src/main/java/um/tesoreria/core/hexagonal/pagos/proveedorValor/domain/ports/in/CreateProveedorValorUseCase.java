package um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.model.ProveedorValor;

public interface CreateProveedorValorUseCase {

    ProveedorValor createProveedorValor(ProveedorValor proveedorValor);
}
