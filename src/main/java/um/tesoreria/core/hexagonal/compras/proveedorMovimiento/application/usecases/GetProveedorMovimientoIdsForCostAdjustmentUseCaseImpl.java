package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.GetProveedorMovimientoIdsForCostAdjustmentUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetProveedorMovimientoIdsForCostAdjustmentUseCaseImpl implements GetProveedorMovimientoIdsForCostAdjustmentUseCase {
    private final ProveedorMovimientoRepository repository;

    @Override
    public List<Long> getProveedorMovimientoIdsForCostAdjustment() {
        return repository.findDistinctProveedorMovimientoIdsForCostAdjustment();
    }
}
