package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in;

import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ResultadoImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.SolicitudImportacion;

public interface ImportarArchivoUseCase {

    ResultadoImportacion importar(SolicitudImportacion solicitud);

}
