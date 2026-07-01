package um.tesoreria.core.hexagonal.lectivo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.in.GetLectivoByFechaUseCase;
import um.tesoreria.core.hexagonal.lectivo.domain.ports.out.LectivoRepository;
import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetLectivoByFechaUseCaseImpl implements GetLectivoByFechaUseCase {
    private final LectivoRepository repository;
    @Override
    public Optional<Lectivo> getLectivoByFecha(OffsetDateTime fecha) {
        return repository.findByFecha(fecha);
    }
}
