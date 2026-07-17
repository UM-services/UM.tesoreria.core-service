package um.tesoreria.core.hexagonal.compras.articulo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in.GetAllArticulosUseCase;
import um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out.ArticuloRepository;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllArticulosUseCaseImpl implements GetAllArticulosUseCase {
    private final ArticuloRepository repository;
    @Override
    public List<Articulo> getAllArticulos() {
        return repository.findAll();
    }
}
