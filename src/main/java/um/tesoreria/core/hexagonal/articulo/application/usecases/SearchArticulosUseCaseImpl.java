package um.tesoreria.core.hexagonal.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.articulo.domain.model.ArticuloSearch;
import um.tesoreria.core.hexagonal.articulo.domain.ports.in.SearchArticulosUseCase;
import um.tesoreria.core.hexagonal.articulo.domain.ports.out.ArticuloRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchArticulosUseCaseImpl implements SearchArticulosUseCase {

    private final ArticuloRepository repository;

    @Override
    public List<ArticuloSearch> searchArticulos(List<String> conditions) {
        return repository.findAllByStrings(conditions);
    }
}
