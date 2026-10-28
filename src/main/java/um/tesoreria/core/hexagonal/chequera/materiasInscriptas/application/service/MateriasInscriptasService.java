package um.tesoreria.core.hexagonal.chequera.materiasInscriptas.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.MateriasEstado;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.ports.in.GetMateriasInscriptasUseCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MateriasInscriptasService {

    private final GetMateriasInscriptasUseCase getMateriasInscriptasUseCase;

    public List<MateriasEstado> getMateriasInscriptas(Integer facultadId, Integer tipoChequeraId,
                                                      Long chequeraSerieId) {
        return getMateriasInscriptasUseCase.getMateriasInscriptas(facultadId, tipoChequeraId, chequeraSerieId);
    }
}
