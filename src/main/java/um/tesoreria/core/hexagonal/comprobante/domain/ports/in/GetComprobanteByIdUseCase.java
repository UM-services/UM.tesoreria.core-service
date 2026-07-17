package um.tesoreria.core.hexagonal.comprobante.domain.ports.in;

import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import java.util.Optional;

public interface GetComprobanteByIdUseCase {
    Optional<Comprobante> getComprobanteById(Integer comprobanteId);
}
