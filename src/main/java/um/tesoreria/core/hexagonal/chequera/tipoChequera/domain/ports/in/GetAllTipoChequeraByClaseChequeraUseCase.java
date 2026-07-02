package um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import java.util.List;

public interface GetAllTipoChequeraByClaseChequeraUseCase {
    List<TipoChequera> getAllTipoChequeraByClaseChequera(Integer claseChequeraId);
    List<TipoChequera> getAllTipoChequeraByClaseChequeraIds(List<Integer> claseChequeraIds);
}
