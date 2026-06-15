package um.tesoreria.core.hexagonal.umhub.reservaVacante.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.umhub.campanha.application.service.CampanhaService;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.model.Campanha;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.application.exception.ReservaVacanteException;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in.FindReservaVacanteUseCase;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.out.ReservaVacanteRepository;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class FindReservaVacanteUseCaseImpl implements FindReservaVacanteUseCase {

    private final ReservaVacanteRepository repository;
    private final PersonaService personaService;
    private final DomicilioService domicilioService;

    @Override
    public ReservaVacante findByReservaVacanteId(UUID reservaVacanteId) {
        log.debug("\n\nFinding ReservaVacante by ID: {}\n\n", reservaVacanteId);
        ReservaVacante reservaVacante = repository.findByReservaVacanteId(reservaVacanteId)
                .orElseThrow(() -> new ReservaVacanteException(reservaVacanteId));
        reservaVacante.setCampanha(reservaVacante.getCampanha());
        Persona persona = personaService.findByUniqueId(reservaVacante.getPersonaUniqueId());
        reservaVacante.setPersona(persona);
        Domicilio domicilio = domicilioService.findByUnique(persona.getPersonaId(), persona.getDocumentoId());
        reservaVacante.setDomicilio(domicilio);
        log.debug("ReservaVacante -> {}", reservaVacante.jsonify());
        return reservaVacante;
    }
}
