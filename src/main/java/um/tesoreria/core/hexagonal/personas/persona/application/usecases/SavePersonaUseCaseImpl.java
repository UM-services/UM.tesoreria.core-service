package um.tesoreria.core.hexagonal.personas.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.persona.application.exception.PersonaException;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaNombresNormalizer;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.in.SavePersonaUseCase;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.out.PersonaRepository;

@Component
@RequiredArgsConstructor
public class SavePersonaUseCaseImpl implements SavePersonaUseCase {

    private final PersonaRepository repository;

    @Override
    public Persona create(Persona persona) {
        normalizarNombres(persona);
        normalizarNumeros(persona);
        return repository.save(persona);
    }

    @Override
    public Persona update(Persona newpersona, Long uniqueId) {
        normalizarNombres(newpersona);
        normalizarNumeros(newpersona);
        return repository.findByUniqueId(uniqueId).map(persona -> {
            persona.setPersonaId(newpersona.getPersonaId());
            persona.setDocumentoId(newpersona.getDocumentoId());
            persona.setApellido(newpersona.getApellido());
            persona.setNombre(newpersona.getNombre());
            persona.setSexo(newpersona.getSexo());
            persona.setPrimero(newpersona.getPrimero());
            persona.setCuit(newpersona.getCuit());
            persona.setCbu(newpersona.getCbu());
            persona.setPassword(newpersona.getPassword());
            persona.setHpum(newpersona.getHpum());
            persona.setNumeroPrefijo(newpersona.getNumeroPrefijo());
            persona.setNumeroPosfijo(newpersona.getNumeroPosfijo());
            persona.setGuaraniPersona(newpersona.getGuaraniPersona());
            return repository.save(persona);
        }).orElseThrow(() -> new PersonaException(uniqueId));
    }

    private void normalizarNombres(Persona persona) {
        if (persona == null) {
            return;
        }
        persona.setApellido(PersonaNombresNormalizer.normalizarApellido(persona.getApellido()));
        persona.setNombre(PersonaNombresNormalizer.normalizarNombre(persona.getNombre()));
    }

    private void normalizarNumeros(Persona persona) {
        if (persona == null) {
            return;
        }
        persona.setNumeroPrefijo(Persona.numeroOrEmpty(persona.getNumeroPrefijo()));
        persona.setNumeroPosfijo(Persona.numeroOrEmpty(persona.getNumeroPosfijo()));
    }
}
