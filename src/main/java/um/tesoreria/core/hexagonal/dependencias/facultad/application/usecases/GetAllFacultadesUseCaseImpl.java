package um.tesoreria.core.hexagonal.dependencias.facultad.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.ports.in.GetAllFacultadesUseCase;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.ports.out.FacultadRepository;
import java.util.List;
@Component
@RequiredArgsConstructor
public class GetAllFacultadesUseCaseImpl implements GetAllFacultadesUseCase {
    private final FacultadRepository repository;
    @Override public List<Facultad> getAll() { return repository.findAll(); }
}
