package um.tesoreria.core.hexagonal.pagos.entrega.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.pagos.entrega.application.exception.EntregaException;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.Entrega;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.EntregaAsignacion;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.ports.in.GetEntregaAsignacionesByProveedorMovimientoIdUseCase;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.ports.in.UpdateEntregaAsientoUseCase;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.ports.out.EntregaRepository;

import java.util.List;

@Service("pagosEntregaService")
@RequiredArgsConstructor
public class EntregaService {

    private final EntregaRepository entregaRepository;
    private final GetEntregaAsignacionesByProveedorMovimientoIdUseCase getEntregaAsignaciones;
    private final UpdateEntregaAsientoUseCase updateEntregaAsiento;

    public Entrega findById(Long entregaId) {
        return entregaRepository.findById(entregaId)
                .orElseThrow(() -> new EntregaException(entregaId));
    }

    public List<EntregaAsignacion> findAsignacionesByProveedorMovimientoId(List<Long> proveedorMovimientoIds) {
        return getEntregaAsignaciones.getEntregaAsignaciones(proveedorMovimientoIds);
    }

    public Entrega updateAsiento(Entrega entrega) {
        return updateEntregaAsiento.updateAsiento(entrega);
    }
}
