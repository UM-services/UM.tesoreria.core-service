package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.GetLastProveedorMovimientoByComprobanteUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetLastProveedorMovimientoByComprobanteUseCaseImpl implements GetLastProveedorMovimientoByComprobanteUseCase {

    private final ProveedorMovimientoRepository repository;

    @Override
    public Optional<ProveedorMovimiento> getLastProveedorMovimientoByComprobante(Integer prefijo, Integer comprobanteId) {
        return repository.findTopByPrefijoAndComprobanteIdInOrderByNumeroComprobanteDesc(prefijo, List.of(comprobanteId));
    }
}
