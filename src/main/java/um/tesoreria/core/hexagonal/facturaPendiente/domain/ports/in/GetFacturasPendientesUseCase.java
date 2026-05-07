package um.tesoreria.core.hexagonal.facturaPendiente.domain.ports.in;

import um.tesoreria.core.hexagonal.facturaPendiente.domain.model.FacturaPendiente;
import java.time.OffsetDateTime;
import java.util.List;

public interface GetFacturasPendientesUseCase {
    List<FacturaPendiente> getFacturasPendientes(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta);
}
