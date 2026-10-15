package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.in.GetCompraRecepcionByIdUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.out.CompraRecepcionRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetCompraRecepcionByIdUseCaseImpl implements GetCompraRecepcionByIdUseCase {

    private final CompraRecepcionRepository repository;

    @Override
    public Optional<CompraRecepcion> getById(Long compraRecepcionId) {
        return repository.findById(compraRecepcionId);
    }

}
