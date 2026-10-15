package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.in.ListCompraRecepcionesUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.out.CompraRecepcionRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListCompraRecepcionesUseCaseImpl implements ListCompraRecepcionesUseCase {

    private final CompraRecepcionRepository repository;

    @Override
    public List<CompraRecepcion> listar(CompraRecepcionCriteria criteria) {
        return repository.findByCriteria(criteria);
    }

}
