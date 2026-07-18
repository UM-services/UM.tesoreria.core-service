package um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;

import java.util.Optional;

public interface GetChequeraPagoByIdMercadoPagoUseCase {
    Optional<ChequeraPago> getChequeraPagoByIdMercadoPago(String idMercadoPago);
}
