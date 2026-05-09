package um.tesoreria.core.hexagonal.facultad.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.facultad.domain.ports.in.GetFacultadesFiltradasUseCase;
import um.tesoreria.core.hexagonal.facultad.domain.ports.out.FacultadRepository;
import java.util.Arrays;
import java.util.List;
@Component
@RequiredArgsConstructor
public class GetFacultadesFiltradasUseCaseImpl implements GetFacultadesFiltradasUseCase {
    private final FacultadRepository repository;
    @Override 
    public List<Facultad> getFacultadesFiltradas() { 
        Integer[] facultades = { 1, 2, 3, 4, 5, 6, 14, 15 };
        return repository.findAllIn(Arrays.asList(facultades));
    }
}
