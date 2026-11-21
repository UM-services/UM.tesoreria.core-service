package um.tesoreria.core.hexagonal.pagos.entrega.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.EntregaAsignacion;

import java.util.List;

public interface GetEntregaAsignacionesByProveedorMovimientoIdUseCase {

    /**
     * Asignaciones de costos activas (entregas no anuladas) de las facturas indicadas.
     */
    List<EntregaAsignacion> getEntregaAsignaciones(List<Long> proveedorMovimientoIds);
}
