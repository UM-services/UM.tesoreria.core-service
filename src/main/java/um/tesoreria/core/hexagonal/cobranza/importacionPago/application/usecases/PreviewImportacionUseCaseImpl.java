package um.tesoreria.core.hexagonal.cobranza.importacionPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.application.service.ImportacionPagoService;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.PreviewImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.SolicitudImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in.PreviewImportacionUseCase;

@Component
@RequiredArgsConstructor
public class PreviewImportacionUseCaseImpl implements PreviewImportacionUseCase {

    private final ImportacionPagoService service;

    @Override
    public PreviewImportacion preview(SolicitudImportacion solicitud) {
        return service.preview(solicitud);
    }

}
