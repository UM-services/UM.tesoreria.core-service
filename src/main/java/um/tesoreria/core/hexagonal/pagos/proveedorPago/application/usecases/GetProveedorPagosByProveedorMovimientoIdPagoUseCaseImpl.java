package um.tesoreria.core.hexagonal.pagos.proveedorPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.model.ProveedorPago;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.ports.in.GetProveedorPagosByProveedorMovimientoIdPagoUseCase;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.ports.out.ProveedorPagoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetProveedorPagosByProveedorMovimientoIdPagoUseCaseImpl
        implements GetProveedorPagosByProveedorMovimientoIdPagoUseCase {

    private final ProveedorPagoRepository proveedorPagoRepository;

    @Override
    public List<ProveedorPago> getProveedorPagos(Long proveedorMovimientoIdPago) {
        return proveedorPagoRepository.findAllByProveedorMovimientoIdPago(proveedorMovimientoIdPago);
    }
}
