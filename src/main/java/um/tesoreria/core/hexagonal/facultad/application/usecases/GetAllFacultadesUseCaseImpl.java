package um.tesoreria.core.hexagonal.facultad.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.facultad.domain.ports.in.GetAllFacultadesUseCase;
import um.tesoreria.core.hexagonal.facultad.domain.ports.out.FacultadRepository;
import java.util.List;
@Component
@RequiredArgsConstructor
public class GetAllFacultadesUseCaseImpl implements GetAllFacultadesUseCase {
    private final FacultadRepository repository;
    @Override public List<Facultad> getAll() { return repository.findAll(); }
}
