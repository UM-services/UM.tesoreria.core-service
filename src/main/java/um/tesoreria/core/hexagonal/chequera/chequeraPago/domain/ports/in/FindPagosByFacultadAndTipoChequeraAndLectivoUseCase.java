package um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;

import java.util.List;

public interface FindPagosByFacultadAndTipoChequeraAndLectivoUseCase {
    List<ChequeraPago> findPagosByFacultadAndTipoChequeraAndLectivo(Integer facultadId, Integer tipoChequeraId, Integer lectivoId);
}
