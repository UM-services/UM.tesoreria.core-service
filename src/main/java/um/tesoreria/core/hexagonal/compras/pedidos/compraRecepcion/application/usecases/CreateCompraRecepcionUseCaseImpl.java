package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.in.CreateCompraRecepcionUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.out.CompraRecepcionRepository;

@Component
@RequiredArgsConstructor
public class CreateCompraRecepcionUseCaseImpl implements CreateCompraRecepcionUseCase {

    private final CompraRecepcionRepository repository;

    @Override
    public CompraRecepcion crear(CompraRecepcion recepcion) {
        return repository.save(recepcion);
    }

}
