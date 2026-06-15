package um.tesoreria.core.hexagonal.umhub.reservaVacante.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.client.tesoreria.mercadopago.PreferenceVacanteClient;
import um.tesoreria.core.exception.PersonaException;
import um.tesoreria.core.hexagonal.domicilio.application.exception.DomicilioException;
import um.tesoreria.core.hexagonal.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.umhub.campanha.application.service.CampanhaService;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.model.Campanha;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.application.exception.ReservaVacanteException;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in.CreateReservaVacanteUseCase;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.out.ReservaVacanteRepository;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.dto.ReservaVacanteRequest;
import um.tesoreria.core.service.facade.MercadoPagoCoreService;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateReservaVacanteUseCaseImpl implements CreateReservaVacanteUseCase {

    private final ReservaVacanteRepository repository;
    private final PersonaService personaService;
    private final DomicilioService domicilioService;
    private final CampanhaService campanhaService;
    private final MercadoPagoCoreService mercadoPagoCoreService;
    private final PreferenceVacanteClient preferenceVacanteClient;

    @Override
    public ReservaVacante createReservaVacante(ReservaVacanteRequest reservaVacanteRequest) {
        log.debug("\n\nProcessing CreateReservaVacanteUseCaseImpl.createReservaVacante\n\n");
        // Verificar persona
        Persona persona;
        var personaId = new BigDecimal(reservaVacanteRequest.getNumeroDocumento().replaceAll("\\D+", ""));
        try {
            persona = personaService.findByUnique(personaId, reservaVacanteRequest.getTipoDocumento());
        } catch (PersonaException e) {
            persona = Persona.builder()
                    .personaId(personaId)
                    .documentoId(reservaVacanteRequest.getTipoDocumento())
                    .apellido(reservaVacanteRequest.getApellido())
                    .nombre(reservaVacanteRequest.getNombre())
                    .cbu("")
                    .cuit("")
                    .hpum((byte) 0)
                    .primero((byte) 0)
                    .sexo("")
                    .build();
            persona = personaService.create(persona);
        }
        log.debug("Persona -> {}", persona.jsonify());
        // Verificar domicilio
        Domicilio domicilio;
        try {
            domicilio = domicilioService.findByUnique(persona.getPersonaId(), persona.getDocumentoId());
        } catch (DomicilioException e) {
            domicilio = Domicilio.builder()
                    .personaId(persona.getPersonaId())
                    .documentoId(persona.getDocumentoId())
                    .fecha(OffsetDateTime.now())
                    .emailPersonal(reservaVacanteRequest.getEmail())
                    .emailInstitucional("")
                    .calle("")
                    .puerta("")
                    .codigoPostal("")
                    .piso("")
                    .dpto("")
                    .laboral("")
                    .telefono("")
                    .movil("")
                    .observaciones("")
                    .build();
            domicilio = domicilioService.create(domicilio);
        }
        log.debug("Domicilio -> {}", domicilio.jsonify());
        // Obtener campanha para importe
        Campanha campanha = campanhaService.getCampanhaById(reservaVacanteRequest.getCampanhaId())
                .orElseThrow(() -> new ReservaVacanteException(reservaVacanteRequest.getCampanhaId()));
        // Crea reservaVacante
        OffsetDateTime vencimiento = OffsetDateTime.now(ZoneOffset.ofHours(-3))
                .plusDays(2)
                .with(LocalTime.MAX);
        ReservaVacante reservaVacante = ReservaVacante.builder()
                .personaUniqueId(persona.getUniqueId())
                .campanhaId(reservaVacanteRequest.getCampanhaId())
                .estado("pendiente")
                .importe(campanha.getValorReserva())
                .vencimiento(vencimiento)
                .build();
        reservaVacante = repository.create(reservaVacante);
        reservaVacante.setPersona(persona);
        reservaVacante.setDomicilio(domicilio);
        reservaVacante.setCampanha(campanha);
        log.debug("ReservaVacante -> {}", reservaVacante.jsonify());
        var umPreferenceMPDto = mercadoPagoCoreService.createContextVacante(reservaVacante);
        preferenceVacanteClient.createPreference(umPreferenceMPDto);
        return reservaVacante;
    }
}
