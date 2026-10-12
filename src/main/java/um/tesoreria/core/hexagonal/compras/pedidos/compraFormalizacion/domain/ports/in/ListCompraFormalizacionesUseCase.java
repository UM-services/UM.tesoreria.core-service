package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionCriteria;

import java.util.List;

public interface ListCompraFormalizacionesUseCase {

    List<CompraFormalizacion> listar(CompraFormalizacionCriteria criteria);

}
