package um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import java.util.List;

public interface GetAllTipoChequeraByFacultadAndGeograficaUseCase {
    List<TipoChequera> getAllTipoChequeraByFacultadAndGeografica(Integer facultadId, Integer geograficaId);
}
