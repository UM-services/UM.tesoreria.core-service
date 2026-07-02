package um.tesoreria.core.hexagonal.lectivo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.in.GetAllLectivosUseCase;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.out.LectivoRepository;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllLectivosUseCaseImpl implements GetAllLectivosUseCase {
    private final LectivoRepository repository;
    @Override
    public List<Lectivo> getAllLectivos() {
        return repository.findAll();
    }
}
