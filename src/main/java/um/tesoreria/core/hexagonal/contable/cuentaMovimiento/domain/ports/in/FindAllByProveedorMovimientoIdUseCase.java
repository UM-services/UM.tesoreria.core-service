package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;

import java.util.List;

public interface FindAllByProveedorMovimientoIdUseCase {

    /** Todas las líneas contables ligadas a un comprobante de proveedor ({@code movcon.proveedormovimiento_id}). */
    List<CuentaMovimiento> findAllByProveedorMovimientoId(Long proveedorMovimientoId);
}