package um.tesoreria.core.hexagonal.lectivo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.in.GetLectivoByIdUseCase;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.out.LectivoRepository;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetLectivoByIdUseCaseImpl implements GetLectivoByIdUseCase {
    private final LectivoRepository repository;
    @Override
    public Optional<Lectivo> getLectivoById(Integer lectivoId) {
        return repository.findById(lectivoId);
    }
}
