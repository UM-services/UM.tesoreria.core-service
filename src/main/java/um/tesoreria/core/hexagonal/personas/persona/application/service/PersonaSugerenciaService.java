package um.tesoreria.core.hexagonal.personas.persona.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaSugerencia;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.out.PersonaSugerenciaRepository;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonaSugerenciaService {

    private final PersonaSugerenciaRepository repository;

    public List<PersonaSugerencia> findByUsuario(Long userId, String query, int limite) {
        if (userId == null || userId <= 0 || query == null || query.length() > 120
                || query.codePoints().filter(Character::isLetterOrDigit).count() < 3
                || limite < 1 || limite > 20) {
            throw new IllegalArgumentException("Parámetros de búsqueda inválidos");
        }
        List<String> terms = Arrays.stream(query.strip().split("[\\s,]+"))
                .filter(term -> !term.isBlank())
                .toList();
        return repository.findByUsuarioAndTerms(userId, terms, limite);
    }
}
