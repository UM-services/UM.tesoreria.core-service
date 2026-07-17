package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.GetProveedorMovimientoByIdUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetProveedorMovimientoByIdUseCaseImpl implements GetProveedorMovimientoByIdUseCase {
    private final ProveedorMovimientoRepository repository;

    @Override
    public Optional<ProveedorMovimiento> getProveedorMovimientoById(Long proveedorMovimientoId) {
        return repository.findById(proveedorMovimientoId);
    }
}
