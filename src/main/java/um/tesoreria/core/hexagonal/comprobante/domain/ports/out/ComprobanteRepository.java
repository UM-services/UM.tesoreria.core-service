package um.tesoreria.core.hexagonal.comprobante.domain.ports.out;

import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;

import java.util.List;
import java.util.Optional;

public interface ComprobanteRepository {
    List<Comprobante> findAll();
    List<Comprobante> findAllByOrdenPago();
    List<Comprobante> findAllByTipoTransaccionId(Integer tipoTransaccionId);
    List<Comprobante> findAllByOrdenPagoAndTipoTransaccionId(Integer tipoTransaccionId);
    Optional<Comprobante> findFirstByTipoTransaccionId(Integer tipoTransaccionId);
    Optional<Comprobante> findByComprobanteId(Integer comprobanteId);
}
