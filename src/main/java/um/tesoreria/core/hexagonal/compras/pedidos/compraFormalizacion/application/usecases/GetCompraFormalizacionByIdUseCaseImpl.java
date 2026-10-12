package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.in.GetCompraFormalizacionByIdUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.out.CompraFormalizacionRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetCompraFormalizacionByIdUseCaseImpl implements GetCompraFormalizacionByIdUseCase {

    private final CompraFormalizacionRepository repository;

    @Override
    public Optional<CompraFormalizacion> getById(Long compraFormalizacionId) {
        return repository.findById(compraFormalizacionId);
    }

}
