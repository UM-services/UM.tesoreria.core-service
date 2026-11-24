package um.tesoreria.core.hexagonal.chequera.emision.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.emision.application.service.BorradorConsultaService;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in.ListarBorradoresSedeUseCase;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListarBorradoresSedeUseCaseImpl implements ListarBorradoresSedeUseCase {

    private final BorradorConsultaService service;

    @Override
    public List<BorradorChequera> listar(Integer facultadId, Integer lectivoId, Integer geograficaId) {
        return service.listarPorSede(facultadId, lectivoId, geograficaId);
    }
}
