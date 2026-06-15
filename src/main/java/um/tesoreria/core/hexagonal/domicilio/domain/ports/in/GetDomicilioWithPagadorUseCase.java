package um.tesoreria.core.hexagonal.domicilio.domain.ports.in;

import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;

import java.math.BigDecimal;

public interface GetDomicilioWithPagadorUseCase {
    Domicilio getDomicilioWithPagador(Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId);
}
