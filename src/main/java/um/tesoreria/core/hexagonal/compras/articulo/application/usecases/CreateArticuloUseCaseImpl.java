package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in.CreateArticuloUseCase;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ArticuloRepository;

@Component
@RequiredArgsConstructor
public class CreateArticuloUseCaseImpl implements CreateArticuloUseCase {
    private final ArticuloRepository repository;
    @Override
    @Transactional
    public Articulo createArticulo(Articulo articulo) {
        ArticuloReglas.validarAlta(articulo);
        ArticuloReglas.normalizarNumeros(articulo);
        return repository.create(articulo);
    }
}
