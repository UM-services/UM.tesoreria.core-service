package um.tesoreria.core.hexagonal.chequera.claseChequera.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;

import java.util.Optional;

public interface GetClaseChequeraByIdUseCase {
    Optional<ClaseChequera> getClaseChequeraById(Integer claseChequeraId);
}
