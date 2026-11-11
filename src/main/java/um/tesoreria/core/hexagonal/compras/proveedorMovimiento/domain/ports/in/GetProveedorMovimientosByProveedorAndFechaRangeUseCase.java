package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimientoResumen;

import java.time.OffsetDateTime;
import java.util.List;

public interface GetProveedorMovimientosByProveedorAndFechaRangeUseCase {

    List<ProveedorMovimientoResumen> getProveedorMovimientos(Integer proveedorId, OffsetDateTime desde,
                                                             OffsetDateTime hasta, Integer geograficaId);
}
