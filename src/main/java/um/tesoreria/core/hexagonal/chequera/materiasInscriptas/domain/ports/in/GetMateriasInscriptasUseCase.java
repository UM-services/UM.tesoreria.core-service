package um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.MateriasEstado;

import java.util.List;

public interface GetMateriasInscriptasUseCase {

    /** Un elemento por fuente consultada, en orden fijo. Vacío si la chequera no es de grado. */
    List<MateriasEstado> getMateriasInscriptas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);
}
