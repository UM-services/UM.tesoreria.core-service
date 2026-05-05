package um.tesoreria.core.hexagonal.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.articulo.domain.ports.in.GetPaginatedArticulosUseCase;
import um.tesoreria.core.hexagonal.articulo.domain.ports.out.ArticuloRepository;
import um.tesoreria.core.model.PaginatedResponse;

@Component
@RequiredArgsConstructor
public class GetPaginatedArticulosUseCaseImpl implements GetPaginatedArticulosUseCase {
    private final ArticuloRepository repository;

    @Override
    public PaginatedResponse<Articulo> getPaginatedArticulosByTipo(String tipo, int page, int size) {
        return repository.findAllPaginatedByTipo(tipo, page, size);
    }
}
