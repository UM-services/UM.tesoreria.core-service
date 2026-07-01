package um.tesoreria.core.hexagonal.lectivo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.in.GetAllLectivosByPersonaUseCase;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.out.LectivoRepository;
import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllLectivosByPersonaUseCaseImpl implements GetAllLectivosByPersonaUseCase {
    private final LectivoRepository repository;
    @Override
    public List<Lectivo> getAllLectivosByPersona(BigDecimal personaId, Integer documentoId) {
        return repository.findAllByPersona(personaId, documentoId);
    }
}
