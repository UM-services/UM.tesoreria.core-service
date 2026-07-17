package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.UpdateProveedorMovimientoUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;

@Component
@RequiredArgsConstructor
public class UpdateProveedorMovimientoUseCaseImpl implements UpdateProveedorMovimientoUseCase {
    private final ProveedorMovimientoRepository repository;

    @Override
    public ProveedorMovimiento updateProveedorMovimiento(ProveedorMovimiento proveedorMovimiento, Long proveedorMovimientoId) {
        return repository.findById(proveedorMovimientoId).map(existing -> {
            ProveedorMovimiento updated = ProveedorMovimiento.builder()
                    .proveedorMovimientoId(proveedorMovimientoId)
                    .proveedorId(proveedorMovimiento.getProveedorId())
                    .nombreBeneficiario(proveedorMovimiento.getNombreBeneficiario())
                    .comprobanteId(proveedorMovimiento.getComprobanteId())
                    .fechaComprobante(proveedorMovimiento.getFechaComprobante())
                    .fechaVencimiento(proveedorMovimiento.getFechaVencimiento())
                    .prefijo(proveedorMovimiento.getPrefijo())
                    .numeroComprobante(proveedorMovimiento.getNumeroComprobante())
                    .netoSinDescuento(proveedorMovimiento.getNetoSinDescuento())
                    .descuento(proveedorMovimiento.getDescuento())
                    .neto(proveedorMovimiento.getNeto())
                    .importe(proveedorMovimiento.getImporte())
                    .cancelado(proveedorMovimiento.getCancelado())
                    .fechaContable(proveedorMovimiento.getFechaContable())
                    .ordenContable(proveedorMovimiento.getOrdenContable())
                    .concepto(proveedorMovimiento.getConcepto())
                    .fechaAnulacion(proveedorMovimiento.getFechaAnulacion())
                    .conCargo(proveedorMovimiento.getConCargo())
                    .solicitaFactura(proveedorMovimiento.getSolicitaFactura())
                    .geograficaId(proveedorMovimiento.getGeograficaId())
                    .build();
            return repository.save(updated);
        }).orElseThrow(() -> new um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException(proveedorMovimientoId));
    }
}
