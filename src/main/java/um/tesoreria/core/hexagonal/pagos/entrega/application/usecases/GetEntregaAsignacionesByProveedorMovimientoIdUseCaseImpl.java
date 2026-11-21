package um.tesoreria.core.hexagonal.pagos.entrega.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.Entrega;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.EntregaAsignacion;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.EntregaDetalle;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.ports.in.GetEntregaAsignacionesByProveedorMovimientoIdUseCase;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.ports.out.EntregaDetalleRepository;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.ports.out.EntregaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GetEntregaAsignacionesByProveedorMovimientoIdUseCaseImpl
        implements GetEntregaAsignacionesByProveedorMovimientoIdUseCase {

    private static final Byte NO_ANULADA = 0;

    private final EntregaDetalleRepository entregaDetalleRepository;
    private final EntregaRepository entregaRepository;

    @Override
    public List<EntregaAsignacion> getEntregaAsignaciones(List<Long> proveedorMovimientoIds) {
        if (proveedorMovimientoIds == null || proveedorMovimientoIds.isEmpty()) {
            return List.of();
        }
        List<EntregaDetalle> detalles =
                entregaDetalleRepository.findAllByProveedorMovimientoIdIn(proveedorMovimientoIds);
        if (detalles.isEmpty()) {
            return List.of();
        }
        List<Long> entregaIds = detalles.stream()
                .map(EntregaDetalle::entregaId)
                .distinct()
                .toList();
        Map<Long, Entrega> entregas = entregaRepository.findAllByEntregaIdIn(entregaIds).stream()
                .filter(entrega -> NO_ANULADA.equals(entrega.anulada()))
                .collect(Collectors.toMap(Entrega::entregaId, Function.identity()));
        List<EntregaAsignacion> asignaciones = new ArrayList<>();
        for (EntregaDetalle detalle : detalles) {
            Entrega entrega = entregas.get(detalle.entregaId());
            if (entrega != null) {
                asignaciones.add(new EntregaAsignacion(entrega, detalle));
            }
        }
        return asignaciones;
    }
}
