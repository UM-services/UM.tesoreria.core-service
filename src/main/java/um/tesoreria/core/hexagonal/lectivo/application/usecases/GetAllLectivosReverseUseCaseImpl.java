package um.tesoreria.core.hexagonal.lectivo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.in.GetAllLectivosReverseUseCase;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.out.LectivoRepository;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllLectivosReverseUseCaseImpl implements GetAllLectivosReverseUseCase {
    private final LectivoRepository repository;
    @Override
    public List<Lectivo> getAllLectivosReverse() {
        return repository.findAllReverse();
    }
}
