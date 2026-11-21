package um.tesoreria.core.hexagonal.pagos.proveedorPago.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.model.ProveedorPago;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.ports.in.CreateProveedorPagoUseCase;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.ports.in.GetProveedorPagosByProveedorMovimientoIdFacturaUseCase;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.ports.in.GetProveedorPagosByProveedorMovimientoIdPagoUseCase;

import java.util.List;

@Service("pagosProveedorPagoService")
@RequiredArgsConstructor
public class ProveedorPagoService {

    private final CreateProveedorPagoUseCase createProveedorPagoUseCase;
    private final GetProveedorPagosByProveedorMovimientoIdPagoUseCase getProveedorPagosByProveedorMovimientoIdPagoUseCase;
    private final GetProveedorPagosByProveedorMovimientoIdFacturaUseCase getProveedorPagosByProveedorMovimientoIdFacturaUseCase;

    public ProveedorPago create(ProveedorPago proveedorPago) {
        return createProveedorPagoUseCase.createProveedorPago(proveedorPago);
    }

    public List<ProveedorPago> findAllByProveedorMovimientoIdPago(Long proveedorMovimientoIdPago) {
        return getProveedorPagosByProveedorMovimientoIdPagoUseCase.getProveedorPagos(proveedorMovimientoIdPago);
    }

    public List<ProveedorPago> findAllByProveedorMovimientoIdFactura(Long proveedorMovimientoIdFactura) {
        return getProveedorPagosByProveedorMovimientoIdFacturaUseCase
                .getProveedorPagosByFactura(proveedorMovimientoIdFactura);
    }

    public boolean existsTrazaContableByFactura(Long proveedorMovimientoIdFactura) {
        return !findAllByProveedorMovimientoIdFactura(proveedorMovimientoIdFactura).isEmpty();
    }
}
