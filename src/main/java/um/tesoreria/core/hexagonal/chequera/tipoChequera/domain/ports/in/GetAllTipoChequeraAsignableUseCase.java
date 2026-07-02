package um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import java.util.List;

public interface GetAllTipoChequeraAsignableUseCase {
    List<TipoChequera> getAllTipoChequeraAsignable(Integer facultadId, Integer lectivoId, Integer geograficaId, Integer claseChequeraId);
}
