package um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.ports.in;

import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model.PagoManual;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model.SolicitudPagoManual;

public interface RegistrarPagoManualUseCase {

    PagoManual registrar(SolicitudPagoManual solicitud);

}
