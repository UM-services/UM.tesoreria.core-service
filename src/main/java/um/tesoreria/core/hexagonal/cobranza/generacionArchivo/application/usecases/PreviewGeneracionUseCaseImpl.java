package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.service.GeneracionArchivoService;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.PreviewGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.SolicitudGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.in.PreviewGeneracionUseCase;

@Component
@RequiredArgsConstructor
public class PreviewGeneracionUseCaseImpl implements PreviewGeneracionUseCase {

    private final GeneracionArchivoService service;

    @Override
    public PreviewGeneracion preview(SolicitudGeneracion solicitud) {
        return service.preview(solicitud);
    }

}
