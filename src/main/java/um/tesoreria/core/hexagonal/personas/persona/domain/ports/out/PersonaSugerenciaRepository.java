package um.tesoreria.core.hexagonal.personas.persona.domain.ports.out;

import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaSugerencia;

import java.util.List;

public interface PersonaSugerenciaRepository {
    List<PersonaSugerencia> findByUsuarioAndTerms(Long userId, List<String> terms, int limite);
}
