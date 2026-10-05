package um.tesoreria.core.hexagonal.umhub.consulta.domain.ports.in;

import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.ConsultaDeuda;

import java.math.BigDecimal;
import java.util.Optional;

public interface GetConsultaDeudaUseCase {

    Optional<ConsultaDeuda> findByNumeroDocumento(BigDecimal numeroDocumento, boolean extended);
}
