package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.FindAllByAsientoUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.FindAllByProveedorMovimientoIdUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ComprobanteEsOrdenPagoException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.CompraImpresion;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.CompraImpresion.AsientoLinea;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.CompraImpresion.DetalleImpreso;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in.GetCompraImpresionUseCase;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.ProveedorMovimientoRepository;
import um.tesoreria.core.kotlin.model.ProveedorArticulo;
import um.tesoreria.core.service.ProveedorArticuloService;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Datos de impresión de una factura o nota de crédito de compras. Sólo lectura: el
 * {@code /detalle} que usa la pantalla no cambia.
 */
@Component
@RequiredArgsConstructor
public class GetCompraImpresionUseCaseImpl implements GetCompraImpresionUseCase {

    private static final byte DEBITA = 1;
    private static final int DESDE_EL_PRIMER_ITEM = 0;
    private static final int DEBE_Y_HABER = 2;

    private final ProveedorMovimientoRepository repository;
    private final ProveedorArticuloService proveedorArticuloService;
    private final FindAllByAsientoUseCase findAllByAsientoUseCase;
    private final FindAllByProveedorMovimientoIdUseCase findAllByProveedorMovimientoIdUseCase;

    @Override
    public CompraImpresion getCompraImpresion(Long proveedorMovimientoId) {
        ProveedorMovimiento compra = repository.findById(proveedorMovimientoId)
                .orElseThrow(() -> new ProveedorMovimientoException(proveedorMovimientoId));
        if (ComprobanteFormato.esOrdenPago(compra)) {
            throw new ComprobanteEsOrdenPagoException(proveedorMovimientoId);
        }

        List<DetalleImpreso> detalle = detalle(proveedorMovimientoId);
        List<AsientoLinea> asiento = asiento(compra);
        var proveedor = compra.getProveedor();

        return CompraImpresion.builder()
                .proveedorMovimientoId(proveedorMovimientoId)
                .comprobanteDescripcion(ComprobanteFormato.descripcion(compra))
                .numero(ComprobanteFormato.numero(compra))
                .prefijo(compra.getPrefijo())
                .numeroComprobante(compra.getNumeroComprobante())
                .fechaComprobante(compra.getFechaComprobante())
                .fechaAnulacion(compra.getFechaAnulacion())
                .concepto(compra.getConcepto())
                .proveedorId(compra.getProveedorId())
                .razonSocial(proveedor != null ? proveedor.getRazonSocial() : null)
                .domicilio(proveedor != null ? proveedor.getDomicilio() : null)
                .cuit(proveedor != null ? proveedor.getCuit() : null)
                .importe(compra.getImporte())
                .detalle(detalle)
                .fechaContable(compra.getFechaContable())
                .ordenContable(compra.getOrdenContable())
                .asiento(asiento)
                .totalDebe(sumar(asiento.stream().map(AsientoLinea::debe)))
                .totalHaber(sumar(asiento.stream().map(AsientoLinea::haber)))
                .build();
    }

    private List<DetalleImpreso> detalle(Long proveedorMovimientoId) {
        return proveedorArticuloService.findAllByProveedorMovimientoId(proveedorMovimientoId, false).stream()
                .map(this::renglon)
                .toList();
    }

    private DetalleImpreso renglon(ProveedorArticulo articulo) {
        return new DetalleImpreso(
                articulo.getOrden(),
                articulo.getArticulo() != null ? articulo.getArticulo().getNombre() : null,
                articulo.getConcepto(),
                articulo.getCantidad(),
                articulo.getPrecioUnitario(),
                articulo.getPrecioFinal());
    }

    /**
     * Líneas contables del comprobante, con el debe primero. Igual que el reporte de VB6 se buscan por
     * {@code movcon.proveedormovimiento_id}, que incluye también las líneas de otros asientos ligados al
     * comprobante (rectificaciones); si no hay ninguna se cae a la fecha y orden contable del comprobante.
     */
    private List<AsientoLinea> asiento(ProveedorMovimiento compra) {
        List<CuentaMovimiento> movimientos =
                findAllByProveedorMovimientoIdUseCase.findAllByProveedorMovimientoId(compra.getProveedorMovimientoId());
        if (movimientos.isEmpty() && compra.getFechaContable() != null && compra.getOrdenContable() != null) {
            movimientos = findAllByAsientoUseCase.findAllByAsiento(
                    compra.getFechaContable(), compra.getOrdenContable(), DESDE_EL_PRIMER_ITEM, DEBE_Y_HABER);
        }
        return movimientos.stream()
                .sorted(Comparator
                        .comparing((CuentaMovimiento m) -> m.getDebita() != null && m.getDebita() == DEBITA ? 0 : 1)
                        .thenComparing(CuentaMovimiento::getFechaContable, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(CuentaMovimiento::getOrdenContable, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(CuentaMovimiento::getItem, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::linea)
                .toList();
    }

    private AsientoLinea linea(CuentaMovimiento movimiento) {
        boolean debe = movimiento.getDebita() != null && movimiento.getDebita() == DEBITA;
        BigDecimal importe = ComprobanteFormato.valor(movimiento.getImporte());
        return new AsientoLinea(
                movimiento.getNumeroCuenta(),
                movimiento.getCuenta() != null ? movimiento.getCuenta().getNombre() : null,
                debe ? importe : BigDecimal.ZERO,
                debe ? BigDecimal.ZERO : importe);
    }

    private static BigDecimal sumar(Stream<BigDecimal> importes) {
        return importes.filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}