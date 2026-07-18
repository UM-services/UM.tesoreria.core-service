package um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;

import java.util.Optional;

public interface GetChequeraPagoByIdUseCase {
    Optional<ChequeraPago> getChequeraPagoById(Long chequeraPagoId);
}
