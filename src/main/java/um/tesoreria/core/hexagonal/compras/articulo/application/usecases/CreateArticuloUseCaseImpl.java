package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in.CreateArticuloUseCase;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ArticuloRepository;

@Component
@RequiredArgsConstructor
public class CreateArticuloUseCaseImpl implements CreateArticuloUseCase {
    private final ArticuloRepository repository;
    @Override
    public Articulo createArticulo(Articulo articulo) {
        return repository.create(articulo);
    }
}
