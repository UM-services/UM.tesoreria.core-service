package um.tesoreria.core.hexagonal.ubicacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.ubicacion.domain.model.Ubicacion;
import um.tesoreria.core.hexagonal.ubicacion.domain.ports.in.GetAllUbicacionesUseCase;
import um.tesoreria.core.hexagonal.ubicacion.domain.ports.out.UbicacionRepository;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllUbicacionesUseCaseImpl implements GetAllUbicacionesUseCase {
    private final UbicacionRepository repository;

    @Override
    public List<Ubicacion> getAllUbicaciones() {
        return repository.findAll();
    }
}
