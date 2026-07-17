package um.tesoreria.core.hexagonal.compras.facturaPendiente.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.facturaPendiente.domain.model.FacturaPendiente;
import um.tesoreria.core.hexagonal.compras.facturaPendiente.domain.ports.in.GetFacturasPendientesUseCase;
import um.tesoreria.core.hexagonal.compras.facturaPendiente.domain.ports.out.FacturaPendienteRepository;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetFacturasPendientesUseCaseImpl implements GetFacturasPendientesUseCase {
    private final FacturaPendienteRepository repository;

    @Override
    public List<FacturaPendiente> getFacturasPendientes(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta) {
        repository.updateFechaPagoInProveedorPago();
        return repository.findFacturasPendientes(fechaDesde, fechaHasta);
    }
}
