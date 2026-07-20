package um.tesoreria.core.hexagonal.chequera.claseChequera.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;

import java.util.List;

public interface GetAllClaseChequeraByTramiteUseCase {
    List<ClaseChequera> getAllClaseChequeraByTramite();
}
