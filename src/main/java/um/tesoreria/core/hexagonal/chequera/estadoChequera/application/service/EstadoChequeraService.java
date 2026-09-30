package um.tesoreria.core.hexagonal.chequera.estadoChequera.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.EstadoChequera;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.ports.in.GetEstadoChequeraUseCase;

@Service
@RequiredArgsConstructor
public class EstadoChequeraService {

    private final GetEstadoChequeraUseCase getEstadoChequeraUseCase;

    public EstadoChequera getEstadoChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                            Integer alternativaId, Integer debitoTipoId) {
        return getEstadoChequeraUseCase.getEstadoChequera(facultadId, tipoChequeraId, chequeraSerieId,
                alternativaId, debitoTipoId);
    }
}