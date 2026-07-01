package um.tesoreria.core.hexagonal.chequera.producto.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto;
import um.tesoreria.core.hexagonal.chequera.producto.domain.ports.in.GetAllProductosUseCase;
import um.tesoreria.core.hexagonal.chequera.producto.domain.ports.out.ProductoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllProductosUseCaseImpl implements GetAllProductosUseCase {
    private final ProductoRepository repository;

    @Override
    public List<Producto> getAllProductos() {
        return repository.findAll();
    }
}
