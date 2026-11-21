package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ReconstruccionPlan;

/**
 * Reconstrucción de la traza contable de una orden de pago. Crea los asientos faltantes y
 * completa los existentes sin tocar líneas de otros comprobantes.
 */
public interface ReconstruirTrazaUseCase {

    ReconstruccionPlan reconstruir(Long proveedorMovimientoId, boolean forzar);
}
