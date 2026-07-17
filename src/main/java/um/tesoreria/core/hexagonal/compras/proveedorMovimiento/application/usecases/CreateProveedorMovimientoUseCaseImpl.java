package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.CreateProveedorMovimientoUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;

@Component
@RequiredArgsConstructor
public class CreateProveedorMovimientoUseCaseImpl implements CreateProveedorMovimientoUseCase {
    private final ProveedorMovimientoRepository repository;

    @Override
    public ProveedorMovimiento createProveedorMovimiento(ProveedorMovimiento proveedorMovimiento) {
        return repository.save(proveedorMovimiento);
    }
}
