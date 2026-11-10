package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.AnulacionComprobante;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimientoDetalle;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimientoResumen;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.GetProveedorMovimientoDetalleUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;
import um.tesoreria.core.kotlin.model.ProveedorPago;
import um.tesoreria.core.kotlin.model.ProveedorValor;
import um.tesoreria.core.kotlin.model.ValorMovimiento;
import um.tesoreria.core.service.EntregaDetalleService;
import um.tesoreria.core.service.ProveedorPagoService;
import um.tesoreria.core.service.ProveedorValorService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Detalle de un comprobante de proveedor: pagos aplicados cuando es una factura,
 * valores cuando es una orden de pago, y el estado de anulación resuelto en el
 * backend (el front sólo traduce los motivos).
 */
@Component
@RequiredArgsConstructor
public class GetProveedorMovimientoDetalleUseCaseImpl implements GetProveedorMovimientoDetalleUseCase {

    private final ProveedorMovimientoRepository repository;
    private final ProveedorPagoService proveedorPagoService;
    private final ProveedorValorService proveedorValorService;
    private final EntregaDetalleService entregaDetalleService;

    @Override
    public ProveedorMovimientoDetalle getProveedorMovimientoDetalle(Long proveedorMovimientoId) {
        ProveedorMovimiento movimiento = repository.findById(proveedorMovimientoId)
                .orElseThrow(() -> new ProveedorMovimientoException(proveedorMovimientoId));

        boolean esOrdenPago = ComprobanteFormato.esOrdenPago(movimiento);

        List<ProveedorPago> pagos = esOrdenPago
                ? List.of()
                : proveedorPagoService.findAllByFactura(proveedorMovimientoId);

        List<ValorMovimiento> valores = esOrdenPago
                ? valoresDeOrdenPago(proveedorMovimientoId)
                : List.of();

        boolean tieneCostos = !entregaDetalleService
                .findAllByProveedorMovimientoId(proveedorMovimientoId)
                .isEmpty();

        return ProveedorMovimientoDetalle.builder()
                .proveedorMovimientoId(proveedorMovimientoId)
                .tipo(esOrdenPago
                        ? ProveedorMovimientoDetalle.TIPO_ORDEN_PAGO
                        : ProveedorMovimientoDetalle.TIPO_FACTURA)
                .cabecera(toResumen(movimiento, tieneCostos))
                .pagos(pagos)
                .valores(valores)
                .anulacion(toAnulacion(movimiento, tieneCostos, esOrdenPago))
                .build();
    }

    private List<ValorMovimiento> valoresDeOrdenPago(Long proveedorMovimientoId) {
        return proveedorValorService.findAllByProveedorMovimientoId(proveedorMovimientoId).stream()
                .map(ProveedorValor::getValorMovimiento)
                .filter(Objects::nonNull)
                .toList();
    }

    private ProveedorMovimientoResumen toResumen(ProveedorMovimiento movimiento, boolean tieneCostos) {
        BigDecimal importe = ComprobanteFormato.valor(movimiento.getImporte());
        BigDecimal cancelado = ComprobanteFormato.valor(movimiento.getCancelado());
        List<String> motivos = motivos(movimiento, tieneCostos);

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

    private AnulacionComprobante toAnulacion(ProveedorMovimiento movimiento, boolean tieneCostos,
                                             boolean esOrdenPago) {
        List<String> motivos = motivos(movimiento, tieneCostos);
        return AnulacionComprobante.builder()
                .habilitada(motivos.isEmpty())
                .motivos(motivos)
                .requierePrivilegioOp(esOrdenPago)
                .build();
    }

    private List<String> motivos(ProveedorMovimiento movimiento, boolean tieneCostos) {
        List<String> motivos = new ArrayList<>();
        if (movimiento.getFechaAnulacion() != null) {
            motivos.add(AnulacionComprobante.YA_ANULADA);
        }
        if (tieneCostos) {
            motivos.add(AnulacionComprobante.COSTOS_ASIGNADOS);
        }
        return motivos;
    }
}
