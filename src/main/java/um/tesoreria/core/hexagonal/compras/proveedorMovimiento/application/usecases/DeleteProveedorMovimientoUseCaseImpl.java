package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.DeleteProveedorMovimientoUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;

@Component
@RequiredArgsConstructor
public class DeleteProveedorMovimientoUseCaseImpl implements DeleteProveedorMovimientoUseCase {
    private final ProveedorMovimientoRepository repository;

    @Override
    public void deleteProveedorMovimiento(Long proveedorMovimientoId) {
        repository.deleteById(proveedorMovimientoId);
    }

    @Override
    public void deleteProveedorMovimiento(ProveedorMovimiento proveedorMovimiento) {
        repository.delete(proveedorMovimiento);
    }
}
