package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.service.GeneracionArchivoService;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.ResultadoGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.SolicitudGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.in.GenerarArchivoUseCase;

@Component
@RequiredArgsConstructor
public class GenerarArchivoUseCaseImpl implements GenerarArchivoUseCase {

    private final GeneracionArchivoService service;

    @Override
    public ResultadoGeneracion generar(SolicitudGeneracion solicitud) {
        return service.generar(solicitud);
    }

}
