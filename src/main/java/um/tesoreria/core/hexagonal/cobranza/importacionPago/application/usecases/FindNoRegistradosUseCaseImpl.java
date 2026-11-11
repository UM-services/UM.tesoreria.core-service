package um.tesoreria.core.hexagonal.cobranza.importacionPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.application.service.ImportacionPagoService;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoBanco;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.in.FindNoRegistradosUseCase;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindNoRegistradosUseCaseImpl implements FindNoRegistradosUseCase {

    private final ImportacionPagoService service;

    @Override
    public List<ArchivoBanco> noRegistrados(Integer importacionId) {
        return service.noRegistrados(importacionId);
    }

}
