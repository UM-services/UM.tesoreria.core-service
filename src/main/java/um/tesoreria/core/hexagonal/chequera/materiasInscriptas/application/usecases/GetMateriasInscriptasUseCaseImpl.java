package um.tesoreria.core.hexagonal.chequera.materiasInscriptas.application.usecases;

import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.MateriasEstado;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.ports.in.GetMateriasInscriptasUseCase;

import java.util.List;

@Service
public class GetMateriasInscriptasUseCaseImpl implements GetMateriasInscriptasUseCase {

    /** Contrato sin fuentes conectadas: siempre lista vacía. #487 la reemplaza por el cálculo real. */
    @Override
    public List<MateriasEstado> getMateriasInscriptas(Integer facultadId, Integer tipoChequeraId,
                                                      Long chequeraSerieId) {
        return List.of();
    }
}
