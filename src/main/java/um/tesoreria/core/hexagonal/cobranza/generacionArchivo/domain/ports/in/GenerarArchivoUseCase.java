package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.in;

import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.ResultadoGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.SolicitudGeneracion;

public interface GenerarArchivoUseCase {

    ResultadoGeneracion generar(SolicitudGeneracion solicitud);

}
