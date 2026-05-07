package um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in.SaveUbicacionArticuloUseCase;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out.UbicacionArticuloRepository;

@Component
@RequiredArgsConstructor
public class SaveUbicacionArticuloUseCaseImpl implements SaveUbicacionArticuloUseCase {
    private final UbicacionArticuloRepository repository;
    @Override
    public UbicacionArticulo save(UbicacionArticulo ubicacionArticulo) {
        return repository.save(ubicacionArticulo);
    }
}
