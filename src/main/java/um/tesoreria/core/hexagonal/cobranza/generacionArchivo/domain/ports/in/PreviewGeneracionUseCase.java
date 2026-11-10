package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.in;

import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.PreviewGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.SolicitudGeneracion;

public interface PreviewGeneracionUseCase {

    PreviewGeneracion preview(SolicitudGeneracion solicitud);

}
