package um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in.GetAllUbicacionArticulosUseCase;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out.UbicacionArticuloRepository;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllUbicacionArticulosUseCaseImpl implements GetAllUbicacionArticulosUseCase {
    private final UbicacionArticuloRepository repository;
    @Override
    public List<UbicacionArticulo> getAll() {
        return repository.findAll();
    }
}
