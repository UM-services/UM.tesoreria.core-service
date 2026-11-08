package um.tesoreria.core.hexagonal.cobranza.pagoManual.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.application.service.PagoManualService;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model.PagoManual;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model.SolicitudPagoManual;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.ports.in.RegistrarPagoManualUseCase;

@Component
@RequiredArgsConstructor
public class RegistrarPagoManualUseCaseImpl implements RegistrarPagoManualUseCase {

    private final PagoManualService service;

    @Override
    public PagoManual registrar(SolicitudPagoManual solicitud) {
        return service.registrar(solicitud);
    }

}
