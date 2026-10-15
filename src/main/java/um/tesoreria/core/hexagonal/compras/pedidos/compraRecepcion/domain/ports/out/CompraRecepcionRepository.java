package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionCriteria;

import java.util.List;
import java.util.Optional;

public interface CompraRecepcionRepository {

    CompraRecepcion save(CompraRecepcion recepcion);

    Optional<CompraRecepcion> findById(Long compraRecepcionId);

    /** Entregas registradas sobre una Orden de Compra (para derivar los saldos). */
    List<CompraRecepcion> findByCompraFormalizacionId(Long compraFormalizacionId);

    List<CompraRecepcion> findByCriteria(CompraRecepcionCriteria criteria);

}
