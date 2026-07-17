package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.GetProveedorMovimientosByComprobanteAndFechaRangeUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetProveedorMovimientosByComprobanteAndFechaRangeUseCaseImpl implements GetProveedorMovimientosByComprobanteAndFechaRangeUseCase {
    private final ProveedorMovimientoRepository repository;

    @Override
    public List<ProveedorMovimiento> getProveedorMovimientosByComprobanteAndFechaRange(
            Integer comprobanteId, OffsetDateTime fechaInicio, OffsetDateTime fechaFinal) {
        return repository.findAllByComprobanteIdAndFechaComprobanteBetween(comprobanteId, fechaInicio, fechaFinal);
    }
}
