package um.tesoreria.core.hexagonal.umhub.consulta.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.ConsultaDeuda;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.model.ConsultaPersona;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.ports.in.GetConsultaDeudaUseCase;
import um.tesoreria.core.hexagonal.umhub.consulta.domain.ports.in.GetConsultaPersonaUseCase;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConsultaPersonaDeudaService {

    private final GetConsultaPersonaUseCase getConsultaPersonaUseCase;
    private final GetConsultaDeudaUseCase getConsultaDeudaUseCase;

    public Optional<ConsultaPersona> findPersona(BigDecimal numeroDocumento) {
        return getConsultaPersonaUseCase.findByNumeroDocumento(numeroDocumento);
    }

    public Optional<ConsultaDeuda> findDeuda(BigDecimal numeroDocumento, boolean extended) {
        return getConsultaDeudaUseCase.findByNumeroDocumento(numeroDocumento, extended);
    }
}
