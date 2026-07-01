package um.tesoreria.core.hexagonal.lectivo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.in.CreateLectivoUseCase;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.out.LectivoRepository;

@Component
@RequiredArgsConstructor
public class CreateLectivoUseCaseImpl implements CreateLectivoUseCase {
    private final LectivoRepository repository;
    @Override
    public Lectivo createLectivo(Lectivo lectivo) {
        return repository.create(lectivo);
    }
}
