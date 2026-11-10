package um.tesoreria.core.hexagonal.cobranza.importacionPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.application.service.ImportacionPagoService;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ResultadoEliminacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in.EliminarImportacionUseCase;

@Component
@RequiredArgsConstructor
public class EliminarImportacionUseCaseImpl implements EliminarImportacionUseCase {

    private final ImportacionPagoService service;

    @Override
    public ResultadoEliminacion eliminar(Integer importacionId) {
        return service.eliminar(importacionId);
    }

}
