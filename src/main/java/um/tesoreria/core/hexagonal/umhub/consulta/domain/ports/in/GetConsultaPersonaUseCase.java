package um.tesoreria.core.hexagonal.umhub.consulta.domain.ports.in;

import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.ConsultaPersona;

import java.math.BigDecimal;
import java.util.Optional;

public interface GetConsultaPersonaUseCase {

    Optional<ConsultaPersona> findByNumeroDocumento(BigDecimal numeroDocumento);
}
