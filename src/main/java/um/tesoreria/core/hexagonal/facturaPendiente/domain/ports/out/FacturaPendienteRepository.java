package um.tesoreria.core.hexagonal.facturaPendiente.domain.ports.out;

import um.tesoreria.core.hexagonal.facturaPendiente.domain.model.FacturaPendiente;
import java.time.OffsetDateTime;
import java.util.List;

public interface FacturaPendienteRepository {
    void updateFechaPagoInProveedorPago();
    List<FacturaPendiente> findFacturasPendientes(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta);
}
