package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.exception.CuentaMovimientoException;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.UpdateCuentaMovimientoUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

@Component
@RequiredArgsConstructor
public class UpdateCuentaMovimientoUseCaseImpl implements UpdateCuentaMovimientoUseCase {

    private final CuentaMovimientoRepository cuentaMovimientoRepository;

    @Override
    public CuentaMovimiento updateCuentaMovimiento(CuentaMovimiento newCuentaMovimiento, Long cuentaMovimientoId) {
        return cuentaMovimientoRepository.findByCuentaMovimientoId(cuentaMovimientoId)
                .map(existente -> {
                    var actualizado = CuentaMovimiento.builder()
                            .cuentaMovimientoId(cuentaMovimientoId)
                            .fechaContable(newCuentaMovimiento.getFechaContable())
                            .ordenContable(newCuentaMovimiento.getOrdenContable())
                            .item(newCuentaMovimiento.getItem())
                            .numeroCuenta(newCuentaMovimiento.getNumeroCuenta())
                            .debita(newCuentaMovimiento.getDebita())
                            .comprobanteId(newCuentaMovimiento.getComprobanteId())
                            .concepto(newCuentaMovimiento.getConcepto())
                            .importe(newCuentaMovimiento.getImporte())
                            .proveedorId(newCuentaMovimiento.getProveedorId())
                            .numeroAnulado(newCuentaMovimiento.getNumeroAnulado())
                            .version(newCuentaMovimiento.getVersion())
                            .proveedorMovimientoId(newCuentaMovimiento.getProveedorMovimientoId())
                            .proveedorMovimientoIdOrdenPago(newCuentaMovimiento.getProveedorMovimientoIdOrdenPago())
                            .apertura(newCuentaMovimiento.getApertura())
                            .trackId(newCuentaMovimiento.getTrackId())
                            .build();
                    return cuentaMovimientoRepository.save(actualizado);
                })
                .orElseThrow(() -> new CuentaMovimientoException(cuentaMovimientoId));
    }
}
