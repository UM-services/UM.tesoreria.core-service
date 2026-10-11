package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedor;

import java.util.List;
import java.util.Optional;

public interface CompraCotizacionRepository {

    CompraCotizacion save(CompraCotizacion proceso);

    /** Actualiza sólo la cabecera (p. ej. la recomendación), sin tocar ítems ni proveedores. */
    CompraCotizacion updateHeader(CompraCotizacion proceso);

    Optional<CompraCotizacion> findById(Long compraCotizacionId);

    List<CompraCotizacion> findByCriteria(CompraCotizacionCriteria criteria);

    /** Documento de proveedor con su detalle de respuesta. */
    Optional<CompraCotizacionProveedor> findProveedorById(Long compraCotizacionProveedorId);

    /** Guarda la cabecera de la respuesta y reemplaza el detalle de precios del proveedor. */
    CompraCotizacionProveedor saveProveedor(CompraCotizacionProveedor proveedor);

    /** Ids de pedido distintos alcanzados por los ítems del proceso. */
    List<Integer> findPedidoIdsByProceso(Long compraCotizacionId);

    /** {@code true} si todos los ítems del pedido están en algún proceso enviado o posterior. */
    boolean todosLosItemsEnProcesosEnviados(Integer compraPedidoId);

    /** Suma de los montos estimados de los pedidos distintos alcanzados por los ítems. */
    java.math.BigDecimal montoEstimadoDeItems(java.util.List<Integer> compraPedidoItemIds);
}
