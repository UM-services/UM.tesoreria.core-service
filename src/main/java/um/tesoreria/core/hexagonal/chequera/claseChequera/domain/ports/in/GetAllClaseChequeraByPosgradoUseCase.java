package um.tesoreria.core.hexagonal.chequera.claseChequera.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;

import java.util.List;

public interface GetAllClaseChequeraByPosgradoUseCase {
    List<ClaseChequera> getAllClaseChequeraByPosgrado();
}
