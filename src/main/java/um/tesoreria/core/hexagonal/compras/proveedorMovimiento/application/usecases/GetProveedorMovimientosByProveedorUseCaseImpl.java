package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.GetProveedorMovimientosByProveedorUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetProveedorMovimientosByProveedorUseCaseImpl implements GetProveedorMovimientosByProveedorUseCase {
    private final ProveedorMovimientoRepository repository;

    @Override
    public List<ProveedorMovimiento> getProveedorMovimientosByProveedor(Integer proveedorId, Integer geograficaId) {
        if (geograficaId == 0) {
            return repository.findAllByProveedorId(proveedorId);
        }
        return repository.findAllByProveedorIdAndGeograficaId(proveedorId, geograficaId);
    }
}
