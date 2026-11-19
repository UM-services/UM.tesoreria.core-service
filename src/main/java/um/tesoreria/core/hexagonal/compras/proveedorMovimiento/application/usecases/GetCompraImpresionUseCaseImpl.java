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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
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
     * Asiento del comprobante, neteado por cuenta: se toman todas las líneas ligadas al comprobante
     * ({@code movcon.proveedormovimiento_id}; si no hay ninguna, las de su fecha y orden contable), se suma el debe y
     * el haber de cada cuenta y sólo queda la diferencia, en el debe si es positiva y en el haber si es negativa.
     * Las cuentas que se compensan (mismo importe en el debe y en el haber) no se imprimen. El debe va primero.
     */
    private List<AsientoLinea> asiento(ProveedorMovimiento compra) {
        List<CuentaMovimiento> movimientos =
                findAllByProveedorMovimientoIdUseCase.findAllByProveedorMovimientoId(compra.getProveedorMovimientoId());
        if (movimientos.isEmpty() && compra.getFechaContable() != null && compra.getOrdenContable() != null) {
            movimientos = findAllByAsientoUseCase.findAllByAsiento(
                    compra.getFechaContable(), compra.getOrdenContable(), DESDE_EL_PRIMER_ITEM, DEBE_Y_HABER);
        }

        // neto por cuenta (debe positivo, haber negativo), en el orden en que aparece cada cuenta
        Map<BigDecimal, BigDecimal> netos = new LinkedHashMap<>();
        Map<BigDecimal, String> nombres = new LinkedHashMap<>();
        for (CuentaMovimiento movimiento : movimientos) {
            BigDecimal importe = ComprobanteFormato.valor(movimiento.getImporte());
            boolean debe = movimiento.getDebita() != null && movimiento.getDebita() == DEBITA;
            netos.merge(movimiento.getNumeroCuenta(), debe ? importe : importe.negate(), BigDecimal::add);
            if (movimiento.getCuenta() != null) {
                nombres.putIfAbsent(movimiento.getNumeroCuenta(), movimiento.getCuenta().getNombre());
            }
        }

        List<AsientoLinea> debe = new ArrayList<>();
        List<AsientoLinea> haber = new ArrayList<>();
        netos.forEach((cuenta, neto) -> {
            int signo = neto.signum();
            if (signo > 0) {
                debe.add(new AsientoLinea(cuenta, nombres.get(cuenta), neto, BigDecimal.ZERO));
            } else if (signo < 0) {
                haber.add(new AsientoLinea(cuenta, nombres.get(cuenta), BigDecimal.ZERO, neto.negate()));
            }
        });
        debe.addAll(haber);
        return debe;
    }

    private static BigDecimal sumar(Stream<BigDecimal> importes) {
        return importes.filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}