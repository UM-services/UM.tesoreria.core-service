package um.tesoreria.core.hexagonal.lectivo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.in.GetLastLectivoUseCase;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.out.LectivoRepository;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetLastLectivoUseCaseImpl implements GetLastLectivoUseCase {
    private final LectivoRepository repository;
    @Override
    public Optional<Lectivo> getLastLectivo() {
        return repository.findLast();
    }
}
