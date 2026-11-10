package um.tesoreria.core.hexagonal.cobranza.pagoManual.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.application.service.PagoManualService;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model.ResultadoEliminacionPago;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.ports.in.EliminarPagoManualUseCase;

@Component
@RequiredArgsConstructor
public class EliminarPagoManualUseCaseImpl implements EliminarPagoManualUseCase {

    private final PagoManualService service;

    @Override
    public ResultadoEliminacionPago eliminar(Long chequeraPagoId) {
        return service.eliminar(chequeraPagoId);
    }

}
