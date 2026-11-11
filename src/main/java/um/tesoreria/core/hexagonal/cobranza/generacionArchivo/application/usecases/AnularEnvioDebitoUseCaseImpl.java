package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.service.GeneracionArchivoService;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.ResultadoOperacionDebito;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.SolicitudAnulacion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.in.AnularEnvioDebitoUseCase;

@Component
@RequiredArgsConstructor
public class AnularEnvioDebitoUseCaseImpl implements AnularEnvioDebitoUseCase {

    private final GeneracionArchivoService service;

    @Override
    public ResultadoOperacionDebito anular(SolicitudAnulacion solicitud) {
        return service.anular(solicitud);
    }

}
