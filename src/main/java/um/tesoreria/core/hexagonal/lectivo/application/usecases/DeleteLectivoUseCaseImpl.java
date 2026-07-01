package um.tesoreria.core.hexagonal.lectivo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.in.DeleteLectivoUseCase;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.out.LectivoRepository;

@Component
@RequiredArgsConstructor
public class DeleteLectivoUseCaseImpl implements DeleteLectivoUseCase {
    private final LectivoRepository repository;
    @Override
    public void deleteLectivo(Integer lectivoId) {
        repository.deleteById(lectivoId);
    }
}
