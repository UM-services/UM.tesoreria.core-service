package um.tesoreria.core.hexagonal.cobranza.importacionPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.application.service.ImportacionPagoService;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoImportado;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.CanalImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in.ListarImportacionesUseCase;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListarImportacionesUseCaseImpl implements ListarImportacionesUseCase {

    private final ImportacionPagoService service;

    @Override
    public List<ArchivoImportado> listar(CanalImportacion canal) {
        return service.listar(canal);
    }

}
