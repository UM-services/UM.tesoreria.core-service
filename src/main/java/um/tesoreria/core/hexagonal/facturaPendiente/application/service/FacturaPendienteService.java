package um.tesoreria.core.hexagonal.facturaPendiente.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.facturaPendiente.domain.model.FacturaPendiente;
import um.tesoreria.core.hexagonal.facturaPendiente.domain.ports.in.GetFacturasPendientesUseCase;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FacturaPendienteService {
    private final GetFacturasPendientesUseCase getFacturasPendientesUseCase;

    public List<FacturaPendiente> findAllFacturasPendientesBetweenDates(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta) {
        return getFacturasPendientesUseCase.getFacturasPendientes(fechaDesde, fechaHasta);
    }
}
