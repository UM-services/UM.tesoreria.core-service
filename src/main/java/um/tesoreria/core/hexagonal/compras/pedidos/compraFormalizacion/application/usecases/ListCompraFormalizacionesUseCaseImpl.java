package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.in.ListCompraFormalizacionesUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.out.CompraFormalizacionRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListCompraFormalizacionesUseCaseImpl implements ListCompraFormalizacionesUseCase {

    private final CompraFormalizacionRepository repository;

    @Override
    public List<CompraFormalizacion> listar(CompraFormalizacionCriteria criteria) {
        return repository.findByCriteria(criteria);
    }

}
