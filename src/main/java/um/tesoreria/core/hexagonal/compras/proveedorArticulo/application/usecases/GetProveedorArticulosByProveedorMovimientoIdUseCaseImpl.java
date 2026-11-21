package um.tesoreria.core.hexagonal.compras.proveedorArticulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorArticulo.domain.model.ProveedorArticulo;
import um.tesoreria.core.hexagonal.compras.proveedorArticulo.domain.ports.in.GetProveedorArticulosByProveedorMovimientoIdUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorArticulo.domain.ports.out.ProveedorArticuloRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetProveedorArticulosByProveedorMovimientoIdUseCaseImpl
        implements GetProveedorArticulosByProveedorMovimientoIdUseCase {

    private final ProveedorArticuloRepository proveedorArticuloRepository;

    @Override
    public List<ProveedorArticulo> getProveedorArticulos(Long proveedorMovimientoId) {
        return proveedorArticuloRepository.findAllByProveedorMovimientoId(proveedorMovimientoId);
    }

    @Override
    public List<ProveedorArticulo> getProveedorArticulos(List<Long> proveedorMovimientoIds) {
        return proveedorArticuloRepository.findAllByProveedorMovimientoIdIn(proveedorMovimientoIds);
    }
}
