package um.tesoreria.core.hexagonal.compras.proveedorArticulo.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.compras.proveedorArticulo.domain.model.ProveedorArticulo;
import um.tesoreria.core.hexagonal.compras.proveedorArticulo.domain.ports.in.GetProveedorArticulosByProveedorMovimientoIdUseCase;

import java.util.List;

@Service("comprasProveedorArticuloService")
@RequiredArgsConstructor
public class ProveedorArticuloService {

    private final GetProveedorArticulosByProveedorMovimientoIdUseCase getProveedorArticulosUseCase;

    public List<ProveedorArticulo> findAllByProveedorMovimientoId(Long proveedorMovimientoId) {
        return getProveedorArticulosUseCase.getProveedorArticulos(proveedorMovimientoId);
    }

    public List<ProveedorArticulo> findAllByProveedorMovimientoIdIn(List<Long> proveedorMovimientoIds) {
        return getProveedorArticulosUseCase.getProveedorArticulos(proveedorMovimientoIds);
    }
}
