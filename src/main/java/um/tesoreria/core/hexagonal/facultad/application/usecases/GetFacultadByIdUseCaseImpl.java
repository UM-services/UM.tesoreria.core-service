package um.tesoreria.core.hexagonal.facultad.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.facultad.domain.ports.in.GetFacultadByIdUseCase;
import um.tesoreria.core.hexagonal.facultad.domain.ports.out.FacultadRepository;
import java.util.Optional;
@Component
@RequiredArgsConstructor
public class GetFacultadByIdUseCaseImpl implements GetFacultadByIdUseCase {
    private final FacultadRepository repository;
    @Override public Optional<Facultad> getById(Integer id) { return repository.findById(id); }
}
