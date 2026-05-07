package um.tesoreria.core.hexagonal.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.articulo.domain.ports.in.UpdateArticuloUseCase;
import um.tesoreria.core.hexagonal.articulo.domain.ports.out.ArticuloRepository;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateArticuloUseCaseImpl implements UpdateArticuloUseCase {
    private final ArticuloRepository repository;
    @Override
    public Optional<Articulo> updateArticulo(Long id, Articulo articulo) {
        return repository.update(id, articulo);
    }
}
