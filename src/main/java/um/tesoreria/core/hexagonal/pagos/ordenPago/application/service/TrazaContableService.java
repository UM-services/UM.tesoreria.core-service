package um.tesoreria.core.hexagonal.pagos.ordenPago.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ReconstruccionPlan;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in.PreviewReconstruccionUseCase;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in.ReconstruirTrazaUseCase;

/**
 * Reconstrucción de la traza contable de una orden de pago: calcula el diff contra los
 * asientos existentes (dry-run) y, cuando se pide, lo aplica de forma idempotente.
 */
@Service("pagosTrazaContableService")
@RequiredArgsConstructor
public class TrazaContableService {

    private final PreviewReconstruccionUseCase previewReconstruccionUseCase;
    private final ReconstruirTrazaUseCase reconstruirTrazaUseCase;

    public ReconstruccionPlan preview(Long proveedorMovimientoId) {
        return previewReconstruccionUseCase.preview(proveedorMovimientoId);
    }

    public ReconstruccionPlan reconstruir(Long proveedorMovimientoId, boolean forzar) {
        return reconstruirTrazaUseCase.reconstruir(proveedorMovimientoId, forzar);
    }
}
