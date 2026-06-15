package um.tesoreria.core.hexagonal.umhub.reservaVacante.application.usecases;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.exception.PersonaException;
import um.tesoreria.core.hexagonal.domicilio.application.exception.DomicilioException;
import um.tesoreria.core.hexagonal.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in.CreateReservaVacanteUseCase;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.out.ReservaVacanteRepository;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.dto.ReservaVacanteRequest;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateReservaVacanteUseCaseImpl implements CreateReservaVacanteUseCase {

    private final ReservaVacanteRepository repository;
    private final PersonaService personaService;
    private final DomicilioService domicilioService;

    @Override
    @Transactional
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
        // Crea reservaVacante
        ReservaVacante reservaVacante = ReservaVacante.builder()
                .personaUniqueId(persona.getUniqueId())
                .campanhaId(reservaVacanteRequest.getCampanhaId())
                .estado("pendiente")
                .build();
        reservaVacante = repository.create(reservaVacante);
        reservaVacante.setPersona(persona);
        reservaVacante.setDomicilio(domicilio);
        log.debug("ReservaVacante -> {}", reservaVacante.jsonify());
        return reservaVacante;
    }
}
