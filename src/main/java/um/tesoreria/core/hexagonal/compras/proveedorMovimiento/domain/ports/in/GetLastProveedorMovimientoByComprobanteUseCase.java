package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;

import java.util.Optional;

/** Último número registrado para un prefijo y un comprobante (réplica de {@code nextComprobante}). */
public interface GetLastProveedorMovimientoByComprobanteUseCase {

    Optional<ProveedorMovimiento> getLastProveedorMovimientoByComprobante(Integer prefijo, Integer comprobanteId);
}
