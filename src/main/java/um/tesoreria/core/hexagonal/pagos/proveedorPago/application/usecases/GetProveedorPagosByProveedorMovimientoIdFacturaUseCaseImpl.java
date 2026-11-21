package um.tesoreria.core.hexagonal.pagos.proveedorPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.model.ProveedorPago;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.ports.in.GetProveedorPagosByProveedorMovimientoIdFacturaUseCase;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.ports.out.ProveedorPagoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetProveedorPagosByProveedorMovimientoIdFacturaUseCaseImpl
        implements GetProveedorPagosByProveedorMovimientoIdFacturaUseCase {

    private final ProveedorPagoRepository proveedorPagoRepository;

    @Override
    public List<ProveedorPago> getProveedorPagosByFactura(Long proveedorMovimientoIdFactura) {
        return proveedorPagoRepository.findAllByProveedorMovimientoIdFactura(proveedorMovimientoIdFactura);
    }
}
