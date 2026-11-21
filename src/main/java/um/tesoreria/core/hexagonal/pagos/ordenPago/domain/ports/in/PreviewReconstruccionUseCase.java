package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ReconstruccionPlan;

/** Cálculo previo (dry-run) de la reconstrucción de la traza contable de una orden de pago. */
public interface PreviewReconstruccionUseCase {

    ReconstruccionPlan preview(Long proveedorMovimientoId);
}
