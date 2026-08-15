package um.tesoreria.core.hexagonal.dependencias.facultad.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.ports.in.GetFacultadByIdUseCase;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.ports.out.FacultadRepository;
import java.util.Optional;
@Component
@RequiredArgsConstructor
public class GetFacultadByIdUseCaseImpl implements GetFacultadByIdUseCase {
    private final FacultadRepository repository;
    @Override public Optional<Facultad> getById(Integer id) { return repository.findById(id); }
}
