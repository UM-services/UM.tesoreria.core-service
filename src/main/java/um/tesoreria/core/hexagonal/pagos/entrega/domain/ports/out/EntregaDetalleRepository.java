package um.tesoreria.core.hexagonal.pagos.entrega.domain.ports.out;

import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.EntregaDetalle;

import java.util.List;

public interface EntregaDetalleRepository {

    List<EntregaDetalle> findAllByProveedorMovimientoIdIn(List<Long> proveedorMovimientoIds);
}
