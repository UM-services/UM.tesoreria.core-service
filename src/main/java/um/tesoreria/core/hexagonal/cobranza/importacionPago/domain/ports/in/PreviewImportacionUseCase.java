package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in;

import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.PreviewImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.SolicitudImportacion;

public interface PreviewImportacionUseCase {

    PreviewImportacion preview(SolicitudImportacion solicitud);

}
