package um.tesoreria.core.hexagonal.pagos.entrega.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.Entrega;

public interface UpdateEntregaAsientoUseCase {

    Entrega updateAsiento(Entrega entrega);
}
