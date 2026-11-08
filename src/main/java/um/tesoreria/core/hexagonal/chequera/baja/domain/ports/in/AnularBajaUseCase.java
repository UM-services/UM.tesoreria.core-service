package um.tesoreria.core.hexagonal.chequera.baja.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.baja.domain.model.ResultadoAnulacionBaja;

public interface AnularBajaUseCase {

    ResultadoAnulacionBaja anular(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);

}
