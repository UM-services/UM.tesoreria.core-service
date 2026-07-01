package um.tesoreria.core.hexagonal.chequera.producto.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto;
import um.tesoreria.core.hexagonal.chequera.producto.domain.ports.in.GetProductoByIdUseCase;
import um.tesoreria.core.hexagonal.chequera.producto.domain.ports.out.ProductoRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetProductoByIdUseCaseImpl implements GetProductoByIdUseCase {
    private final ProductoRepository repository;

    @Override
    public Optional<Producto> getProductoById(Integer productoId) {
        return repository.findById(productoId);
    }
}
