package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.EnvioChequeraGateway;
import um.tesoreria.core.hexagonal.personas.domicilio.application.service.DomicilioService;
import um.tesoreria.core.service.facade.ChequeraService;
import um.tesoreria.core.service.facade.MailChequeraService;

import java.math.BigDecimal;

/**
 * Anticorrupción del slice: compone los servicios existentes (domicilios y los facades legacy de
 * chequeras/correo) para que la capa de aplicación no dependa de ellos.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class EnvioChequeraGatewayAdapter implements EnvioChequeraGateway {

    private final DomicilioService domicilioService;
    private final ChequeraService chequeraService;
    private final MailChequeraService mailChequeraService;

    @Override
    public Integer capturarCorreos(BigDecimal personaId, Integer documentoId) {
        return domicilioService.capture(personaId, documentoId);
    }

    @Override
    public void extenderDebito(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        chequeraService.extendDebito(facultadId, tipoChequeraId, chequeraSerieId);
    }

    @Override
    public String enviarChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                 Integer alternativaId, boolean codigoBarras, Long jobId) {
        return mailChequeraService.sendChequera(facultadId, tipoChequeraId, chequeraSerieId, alternativaId,
                false, false, codigoBarras, true, true, jobId);
    }
}
