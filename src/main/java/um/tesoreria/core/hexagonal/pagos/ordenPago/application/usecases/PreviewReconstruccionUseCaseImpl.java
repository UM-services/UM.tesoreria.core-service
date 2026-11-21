package um.tesoreria.core.hexagonal.pagos.ordenPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.service.TrazaContableCalculator;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ReconstruccionPlan;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in.PreviewReconstruccionUseCase;

@Component
@RequiredArgsConstructor
public class PreviewReconstruccionUseCaseImpl implements PreviewReconstruccionUseCase {

    private final TrazaContableCalculator trazaContableCalculator;

    @Override
    public ReconstruccionPlan preview(Long proveedorMovimientoId) {
        return trazaContableCalculator.calcular(proveedorMovimientoId);
    }
}
