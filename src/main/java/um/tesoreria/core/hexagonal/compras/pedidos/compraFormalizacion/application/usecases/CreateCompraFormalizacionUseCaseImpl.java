package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.in.CreateCompraFormalizacionUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.out.CompraFormalizacionRepository;

@Component
@RequiredArgsConstructor
public class CreateCompraFormalizacionUseCaseImpl implements CreateCompraFormalizacionUseCase {

    private final CompraFormalizacionRepository repository;

    @Override
    public CompraFormalizacion crear(CompraFormalizacion formalizacion) {
        return repository.save(formalizacion);
    }

}
