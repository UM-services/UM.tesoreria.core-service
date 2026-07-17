package um.tesoreria.core.hexagonal.comprobante.domain.ports.in;

import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import java.util.List;

public interface GetAllComprobantesByTipoTransaccionIdUseCase {
    List<Comprobante> getAllComprobantesByTipoTransaccionId(Integer tipoTransaccionId);
}
