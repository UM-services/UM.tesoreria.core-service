package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.AnulacionComprobante;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimientoResumen;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.GetProveedorMovimientosByProveedorAndFechaRangeUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;
import um.tesoreria.core.kotlin.model.EntregaDetalle;
import um.tesoreria.core.service.EntregaDetalleService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Listado «Movimientos Proveedor»: todos los comprobantes del proveedor en el rango,
 * filtrados por sede cuando {@code geograficaId > 0} (paridad con el legacy).
 *
 * <p>Incluye los comprobantes anulados (también como el legacy), pero los marca con
 * {@code anulable = false} y el motivo {@link AnulacionComprobante#YA_ANULADA}.
 */
@Component
@RequiredArgsConstructor
public class GetProveedorMovimientosByProveedorAndFechaRangeUseCaseImpl
        implements GetProveedorMovimientosByProveedorAndFechaRangeUseCase {

    private final ProveedorMovimientoRepository repository;
    private final EntregaDetalleService entregaDetalleService;

    @Override
    public List<ProveedorMovimientoResumen> getProveedorMovimientos(Integer proveedorId, OffsetDateTime desde,
                                                                    OffsetDateTime hasta, Integer geograficaId) {
        List<ProveedorMovimiento> movimientos = (geograficaId == null || geograficaId == 0)
                ? repository.findAllByProveedorIdAndFechaComprobanteBetween(proveedorId, desde, hasta)
                : repository.findAllByProveedorIdAndFechaComprobanteBetweenAndGeograficaId(
                        proveedorId, desde, hasta, geograficaId);

        Set<Long> conCostos = movimientosConCostosAsignados(movimientos);

        return movimientos.stream()
                .sorted(ordenLegacy())
                .map(movimiento -> toResumen(movimiento, conCostos.contains(movimiento.getProveedorMovimientoId())))
                .collect(Collectors.toList());
    }

    /** Una sola consulta para todo el listado: evita el N+1 de `entrega_detalle`. */
    private Set<Long> movimientosConCostosAsignados(List<ProveedorMovimiento> movimientos) {
        List<Long> ids = movimientos.stream()
                .map(ProveedorMovimiento::getProveedorMovimientoId)
                .filter(Objects::nonNull)
                .toList();
        if (ids.isEmpty()) {
            return Set.of();
        }
        return entregaDetalleService.findAllByProveedorMovimientoIds(ids).stream()
                .map(EntregaDetalle::getProveedorMovimientoId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /** Orden del legacy: fecha descendente y, a igual fecha, las órdenes de pago primero. */
    private Comparator<ProveedorMovimiento> ordenLegacy() {
        return (a, b) -> {
            int porFecha = Comparator.nullsLast(Comparator.<OffsetDateTime>reverseOrder())
                    .compare(a.getFechaComprobante(), b.getFechaComprobante());
            if (porFecha != 0) {
                return porFecha;
            }
            int porOrdenPago = Comparator.nullsLast(Comparator.<Byte>reverseOrder())
                    .compare(ComprobanteFormato.ordenPago(a), ComprobanteFormato.ordenPago(b));
            if (porOrdenPago != 0) {
                return porOrdenPago;
            }
            int porPrefijo = Comparator.nullsLast(Comparator.<Integer>naturalOrder())
                    .compare(a.getPrefijo(), b.getPrefijo());
            if (porPrefijo != 0) {
                return porPrefijo;
            }
            return Comparator.nullsLast(Comparator.<Long>naturalOrder())
                    .compare(a.getNumeroComprobante(), b.getNumeroComprobante());
        };
    }

    private ProveedorMovimientoResumen toResumen(ProveedorMovimiento movimiento, boolean tieneCostos) {
        List<String> motivos = new ArrayList<>();
        if (movimiento.getFechaAnulacion() != null) {
            motivos.add(AnulacionComprobante.YA_ANULADA);
        }
        if (tieneCostos) {
            motivos.add(AnulacionComprobante.COSTOS_ASIGNADOS);
        }
        BigDecimal importe = ComprobanteFormato.valor(movimiento.getImporte());
        BigDecimal cancelado = ComprobanteFormato.valor(movimiento.getCancelado());

        return ProveedorMovimientoResumen.builder()
                .proveedorMovimientoId(movimiento.getProveedorMovimientoId())
                .comprobanteId(movimiento.getComprobanteId())
                .comprobanteDescripcion(ComprobanteFormato.descripcion(movimiento))
                .ordenPago(ComprobanteFormato.ordenPago(movimiento))
                .numero(ComprobanteFormato.numero(movimiento))
                .fechaComprobante(movimiento.getFechaComprobante())
                .importe(importe)
                .cancelado(cancelado)
                .saldo(importe.subtract(cancelado))
                .fechaAnulacion(movimiento.getFechaAnulacion())
                .anulable(motivos.isEmpty())
                .motivosNoAnulable(motivos)
                .requierePrivilegioOp(ComprobanteFormato.esOrdenPago(movimiento))
                .build();
    }
}
