package um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;

public interface UpdateChequeraPagoUseCase {

    ChequeraPago updateChequeraPago(ChequeraPago chequeraPago);

}
