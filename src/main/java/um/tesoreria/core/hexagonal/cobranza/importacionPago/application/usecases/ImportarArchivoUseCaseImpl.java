package um.tesoreria.core.hexagonal.cobranza.importacionPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.application.service.ImportacionPagoService;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ResultadoImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.SolicitudImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in.ImportarArchivoUseCase;

@Component
@RequiredArgsConstructor
public class ImportarArchivoUseCaseImpl implements ImportarArchivoUseCase {

    private final ImportacionPagoService service;

    @Override
    public ResultadoImportacion importar(SolicitudImportacion solicitud) {
        return service.importar(solicitud);
    }

}
