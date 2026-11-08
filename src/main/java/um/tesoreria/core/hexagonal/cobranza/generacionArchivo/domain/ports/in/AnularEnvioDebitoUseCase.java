package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.in;

import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.ResultadoOperacionDebito;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.SolicitudAnulacion;

public interface AnularEnvioDebitoUseCase {

    ResultadoOperacionDebito anular(SolicitudAnulacion solicitud);

}
