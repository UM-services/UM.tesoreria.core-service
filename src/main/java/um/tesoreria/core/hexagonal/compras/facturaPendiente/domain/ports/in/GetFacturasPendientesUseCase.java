package um.tesoreria.core.hexagonal.compras.facturaPendiente.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.facturaPendiente.domain.model.FacturaPendiente;
import java.time.OffsetDateTime;
import java.util.List;

public interface GetFacturasPendientesUseCase {
    List<FacturaPendiente> getFacturasPendientes(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta);
}
