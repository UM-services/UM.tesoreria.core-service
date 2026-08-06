package um.tesoreria.core.hexagonal.personas.persona.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import java.util.List;

public interface FindAllSantanderUseCase {
    List<Persona> findAllSantander();
}
