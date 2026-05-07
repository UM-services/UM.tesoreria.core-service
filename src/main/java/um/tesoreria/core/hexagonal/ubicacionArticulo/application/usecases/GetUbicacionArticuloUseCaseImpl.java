package um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in.GetUbicacionArticuloUseCase;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out.UbicacionArticuloRepository;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetUbicacionArticuloUseCaseImpl implements GetUbicacionArticuloUseCase {
    private final UbicacionArticuloRepository repository;
    @Override
    public Optional<UbicacionArticulo> getByUbicacionAndArticulo(Integer ubicacionId, Long articuloId) {
        return repository.findByUbicacionIdAndArticuloId(ubicacionId, articuloId);
    }
}
