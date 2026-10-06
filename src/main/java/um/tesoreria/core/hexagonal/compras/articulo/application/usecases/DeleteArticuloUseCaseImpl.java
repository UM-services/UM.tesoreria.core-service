package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in.DeleteArticuloUseCase;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ArticuloRepository;

@Component
@RequiredArgsConstructor
public class DeleteArticuloUseCaseImpl implements DeleteArticuloUseCase {
    private final ArticuloRepository repository;
    @Override
    @Transactional
    public void deleteArticulo(Long id) {
        repository.findByIdForUpdate(id).orElseThrow(() -> new ArticuloException(id));
        repository.deleteById(id);
    }
}
