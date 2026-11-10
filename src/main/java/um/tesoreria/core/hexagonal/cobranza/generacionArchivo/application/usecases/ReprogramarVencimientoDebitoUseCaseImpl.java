package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.service.GeneracionArchivoService;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.ResultadoOperacionDebito;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.SolicitudReprogramacion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.in.ReprogramarVencimientoDebitoUseCase;

@Component
@RequiredArgsConstructor
public class ReprogramarVencimientoDebitoUseCaseImpl implements ReprogramarVencimientoDebitoUseCase {

    private final GeneracionArchivoService service;

    @Override
    public ResultadoOperacionDebito reprogramar(SolicitudReprogramacion solicitud) {
        return service.reprogramar(solicitud);
    }

}
