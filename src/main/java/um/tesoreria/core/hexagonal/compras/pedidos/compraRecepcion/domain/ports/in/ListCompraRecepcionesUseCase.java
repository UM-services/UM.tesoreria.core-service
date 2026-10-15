package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionCriteria;

import java.util.List;

public interface ListCompraRecepcionesUseCase {

    List<CompraRecepcion> listar(CompraRecepcionCriteria criteria);

}
