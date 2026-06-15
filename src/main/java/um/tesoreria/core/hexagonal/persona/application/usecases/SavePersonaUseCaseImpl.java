package um.tesoreria.core.hexagonal.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.exception.PersonaException;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.persona.domain.ports.in.SavePersonaUseCase;
import um.tesoreria.core.hexagonal.persona.domain.ports.out.PersonaRepository;

@Component
@RequiredArgsConstructor
public class SavePersonaUseCaseImpl implements SavePersonaUseCase {

    private final PersonaRepository repository;

    @Override
    public Persona create(Persona persona) {
        return repository.save(persona);
    }

    @Override
    public Persona update(Persona newpersona, Long uniqueId) {
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
            return repository.save(persona);
        }).orElseThrow(() -> new PersonaException(uniqueId));
    }
}
