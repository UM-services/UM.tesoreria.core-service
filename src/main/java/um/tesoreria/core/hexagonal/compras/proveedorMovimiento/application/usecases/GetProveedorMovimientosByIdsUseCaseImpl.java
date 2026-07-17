package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.GetProveedorMovimientosByIdsUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetProveedorMovimientosByIdsUseCaseImpl implements GetProveedorMovimientosByIdsUseCase {
    private final ProveedorMovimientoRepository repository;

    @Override
    public List<ProveedorMovimiento> getProveedorMovimientosByIds(List<Long> proveedorMovimientoIds) {
        return repository.findAllByIds(proveedorMovimientoIds);
    }
}
