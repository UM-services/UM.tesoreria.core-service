package um.tesoreria.core.hexagonal.ubicacionArticulo.application.usecases;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.in.GetUbicacionArticulosByArticuloUseCase;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.ports.out.UbicacionArticuloRepository;
import java.util.List;
@Component
@RequiredArgsConstructor
public class GetUbicacionArticulosByArticuloUseCaseImpl implements GetUbicacionArticulosByArticuloUseCase {
    private final UbicacionArticuloRepository repository;
    @Override
    public List<UbicacionArticulo> getByArticuloId(Long articuloId) {
        return repository.findAllByArticuloId(articuloId);
    }
}
