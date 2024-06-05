package um.tesoreria.core.service.view;

import org.springframework.stereotype.Service;
import um.tesoreria.core.kotlin.model.view.FacturaPendiente;
import um.tesoreria.core.kotlin.repository.view.FacturaPendienteRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class FacturaPendienteService {

    private final FacturaPendienteRepository repository;

    public FacturaPendienteService(FacturaPendienteRepository repository) {
        this.repository = repository;
    }

    public List<FacturaPendiente> findAllFacturasPendientesBetweenDates(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta) {
        return repository.findFacturasPendientes(fechaDesde, fechaHasta);
    }

}
