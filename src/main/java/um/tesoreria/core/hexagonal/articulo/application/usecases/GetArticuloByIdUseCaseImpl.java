package um.tesoreria.core.hexagonal.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.articulo.domain.ports.in.GetArticuloByIdUseCase;
import um.tesoreria.core.hexagonal.articulo.domain.ports.out.ArticuloRepository;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetArticuloByIdUseCaseImpl implements GetArticuloByIdUseCase {
    private final ArticuloRepository repository;
    @Override
    public Optional<Articulo> getArticuloById(Long id) {
        return repository.findById(id);
    }
}
