package um.tesoreria.core.hexagonal.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.articulo.domain.ports.in.DeleteArticuloUseCase;
import um.tesoreria.core.hexagonal.articulo.domain.ports.out.ArticuloRepository;

@Component
@RequiredArgsConstructor
public class DeleteArticuloUseCaseImpl implements DeleteArticuloUseCase {
    private final ArticuloRepository repository;
    @Override
    public boolean deleteArticulo(Long id) {
        return repository.deleteById(id);
    }
}
