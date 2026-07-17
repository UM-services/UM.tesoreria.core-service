package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.GetProveedorMovimientosDisponiblesUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetProveedorMovimientosDisponiblesUseCaseImpl implements GetProveedorMovimientosDisponiblesUseCase {
    private final ProveedorMovimientoRepository repository;

    @Override
    public List<ProveedorMovimiento> getProveedorMovimientosDisponibles(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta) {
        return repository.findAllByFechaComprobanteBetweenAndFechaAnulacionIsNullAndComprobanteIdNot(fechaDesde, fechaHasta, 6);
    }
}
