package um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.EstadoChequera;

public interface GetEstadoChequeraUseCase {

    EstadoChequera getEstadoChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                     Integer alternativaId);
}