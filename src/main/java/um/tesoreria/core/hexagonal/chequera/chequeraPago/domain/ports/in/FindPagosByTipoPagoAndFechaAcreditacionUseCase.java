package um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;

import java.time.OffsetDateTime;
import java.util.List;

public interface FindPagosByTipoPagoAndFechaAcreditacionUseCase {
    List<ChequeraPago> findPagosByTipoPagoAndFechaAcreditacion(Integer tipoPagoId, OffsetDateTime fechaAcreditacion);
}
