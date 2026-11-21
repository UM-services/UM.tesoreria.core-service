package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.DeudaEnvio;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.EnvioChequeraGateway;
import um.tesoreria.core.hexagonal.personas.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.service.facade.ChequeraService;
import um.tesoreria.core.service.facade.MailChequeraService;

import java.math.BigDecimal;
import java.util.List;

/**
 * Anticorrupción del slice: compone los servicios existentes (slices de chequeras y personas, y
 * los facades legacy de chequeras/correo) para que la capa de aplicación no dependa de ellos.
 *
 * <p>{@code retenida} se escribe con {@code ChequeraSerieService.marcarRetenida}: una actualización
 * de una sola columna que pasa por JPA (y por lo tanto por la auditoría), nunca el {@code PUT}
 * completo que pisaría beca, {@code hpum} y {@code enviado}.</p>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class EnvioChequeraGatewayAdapter implements EnvioChequeraGateway {

    private final DomicilioService domicilioService;
    private final PersonaService personaService;
    private final ChequeraSerieService chequeraSerieService;
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
    public DeudaEnvio deuda(BigDecimal personaId, Integer documentoId) {
        var deuda = personaService.deudaByPersona(personaId, documentoId);
        if (deuda == null) {
            return DeudaEnvio.builder().personaId(personaId).documentoId(documentoId).deudas(List.of()).build();
        }
        List<DeudaEnvio.DeudaChequeraEnvio> deudas = deuda.getDeudas() == null ? List.of()
                : deuda.getDeudas().stream()
                        .map(d -> DeudaEnvio.DeudaChequeraEnvio.builder()
                                .lectivoId(d.getLectivoId())
                                .cuotas(d.getCuotas())
                                .build())
                        .toList();
        return DeudaEnvio.builder()
                .personaId(personaId)
                .documentoId(documentoId)
                .deudas(deudas)
                .build();
    }

    @Override
    public void marcarRetenida(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, boolean retenida) {
        chequeraSerieService.marcarRetenida(facultadId, tipoChequeraId, chequeraSerieId, retenida);
    }

    @Override
    public String enviarChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                 Integer alternativaId, boolean codigoBarras) {
        return mailChequeraService.sendChequera(facultadId, tipoChequeraId, chequeraSerieId, alternativaId,
                false, false, codigoBarras, true, true);
    }

    @Override
    public String notificarDeuda(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        return mailChequeraService.notificaDeudorChequera(facultadId, tipoChequeraId, chequeraSerieId);
    }
}
