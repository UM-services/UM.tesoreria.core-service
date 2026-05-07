package um.tesoreria.core.hexagonal.facturaPendiente.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.facturaPendiente.domain.model.FacturaPendiente;
import um.tesoreria.core.hexagonal.facturaPendiente.domain.ports.in.GetFacturasPendientesUseCase;
import um.tesoreria.core.hexagonal.facturaPendiente.domain.ports.out.FacturaPendienteRepository;
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
