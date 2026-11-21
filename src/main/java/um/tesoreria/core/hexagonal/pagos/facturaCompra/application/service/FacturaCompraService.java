package um.tesoreria.core.hexagonal.pagos.facturaCompra.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.articulo.application.service.ArticuloService;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.compras.proveedor.application.service.ProveedorService;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.comprobante.application.service.ComprobanteService;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableLinea;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ReservarOrdenContableUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoEntity;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.application.exception.FacturaCompraException;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.application.exception.FacturaCompraException.Codigo;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model.FacturaCompra;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model.FacturaCompraArticulo;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model.FacturaCompraArticuloDetalle;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model.FacturaCompraDetalle;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model.FacturaCompraResultado;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.ports.in.FacturaCompraUseCase;
import um.tesoreria.core.kotlin.model.ProveedorArticulo;
import um.tesoreria.core.service.ProveedorArticuloService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Registro de Facturas de Compras (legacy {@code frmProvCompras} + {@code clsCtlCompras.saveCompra}
 * + {@code clsCtlArticulo.saveCompra}).
 *
 * <p>Paridad: comprobante del proveedor, artículo por artículo, factor de signo según
 * {@code comprobante.debita}, vínculo "Factura de Compra {prefijo}-{numero}", concepto de línea
 * "Fac: {prefijo}-{numero}", líneas con el {@code comprobanteId} de la factura y
 * {@code proveedor_articulo} con precios firmados. La numeración del asiento la decide el core.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FacturaCompraService implements FacturaCompraUseCase {

    private final ProveedorMovimientoService proveedorMovimientoService;
    private final ProveedorArticuloService proveedorArticuloService;
    private final ProveedorService proveedorService;
    private final ComprobanteService comprobanteService;
    private final ArticuloService articuloService;
    private final AsientoService asientoService;
    private final CuentaMovimientoService cuentaMovimientoService;
    private final CuentaService cuentaService;
    private final ReservarOrdenContableUseCase reservarOrdenContable;

    @Override
    @Transactional
    public FacturaCompraResultado registrar(FacturaCompra factura) {
        if (factura.fechaComprobante() == null) {
            throw new FacturaCompraException(Codigo.FECHA_INVALIDA, "La fecha del comprobante es obligatoria");
        }
        if (factura.total() == null || factura.total().signum() == 0) {
            throw new FacturaCompraException(Codigo.TOTAL_INVALIDO, "El total de la factura no puede ser 0");
        }
        List<FacturaCompraArticulo> articulos = factura.articulos() == null ? List.of() : factura.articulos();
        if (articulos.isEmpty()) {
            throw new FacturaCompraException(Codigo.ARTICULOS_INVALIDOS, "La factura no tiene artículos");
        }
        if (factura.proveedorId() == null) {
            throw new FacturaCompraException(Codigo.PROVEEDOR_INEXISTENTE, "Falta el proveedor");
        }
        if (factura.comprobanteId() == null) {
            throw new FacturaCompraException(Codigo.COMPROBANTE_INEXISTENTE, "Falta el tipo de comprobante");
        }
        BigDecimal sumaArticulos = articulos.stream()
                .map(FacturaCompraArticulo::precioFinal)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (sumaArticulos.compareTo(factura.total()) != 0) {
            throw new FacturaCompraException(Codigo.SUMA_ARTICULOS_INVALIDA,
                    "La suma de los artículos (" + sumaArticulos + ") no coincide con el total ("
                            + factura.total() + ")");
        }

        Proveedor proveedor = proveedorService.getByProveedorId(factura.proveedorId())
                .orElseThrow(() -> new FacturaCompraException(Codigo.PROVEEDOR_INEXISTENTE,
                        "No existe el proveedor " + factura.proveedorId()));
        Comprobante comprobante;
        try {
            comprobante = comprobanteService.findByComprobanteId(factura.comprobanteId());
        } catch (RuntimeException e) {
            throw new FacturaCompraException(Codigo.COMPROBANTE_INEXISTENTE,
                    "No existe el comprobante " + factura.comprobanteId());
        }
        if (proveedorMovimientoService.findByComprobante(factura.proveedorId(), factura.comprobanteId(),
                factura.prefijo(), factura.numeroComprobante()).isPresent()) {
            throw new FacturaCompraException(Codigo.FACTURA_DUPLICADA,
                    "La factura " + factura.prefijo() + "-" + factura.numeroComprobante()
                            + " del proveedor " + factura.proveedorId() + " ya existe");
        }

        OffsetDateTime fecha = normalizar(factura.fechaComprobante());
        OffsetDateTime vencimiento = normalizar(
                factura.fechaVencimiento() == null ? factura.fechaComprobante() : factura.fechaVencimiento());
        byte debita = comprobante.getDebita() == null ? 0 : comprobante.getDebita();
        BigDecimal factor = BigDecimal.valueOf(debita == 0 ? 1 : -1);
        Integer orden = reservarOrdenContable.reservar(fecha);

        BigDecimal neto = (factura.neto() == null ? factura.total() : factura.neto()).multiply(factor);
        ProveedorMovimiento movimiento = ProveedorMovimiento.builder()
                .proveedorId(proveedor.getProveedorId())
                .nombreBeneficiario(proveedor.getRazonSocial())
                .comprobanteId(comprobante.getComprobanteId())
                .fechaComprobante(fecha)
                .fechaVencimiento(vencimiento)
                .prefijo(factura.prefijo() == null ? 0 : factura.prefijo())
                .numeroComprobante(factura.numeroComprobante() == null ? 0L : factura.numeroComprobante())
                .netoSinDescuento(neto)
                .descuento(BigDecimal.ZERO)
                .neto(neto)
                .importe(factura.total().multiply(factor))
                .cancelado(BigDecimal.ZERO)
                .fechaContable(fecha)
                .ordenContable(orden)
                .concepto(factura.concepto())
                .conCargo((byte) 0)
                .solicitaFactura((byte) 0)
                .geograficaId(factura.geograficaId())
                .build();
        movimiento = proveedorMovimientoService.createProveedorMovimiento(movimiento);

        String numero = formatear(movimiento.getPrefijo(), movimiento.getNumeroComprobante());
        String vinculo = "Factura de Compra " + numero;
        String conceptoLínea = "Fac: " + numero;

        if (proveedor.getNumeroCuenta() == null) {
            throw new FacturaCompraException(Codigo.CUENTA_FALTANTE,
                    "El proveedor " + proveedor.getProveedorId() + " no tiene cuenta contable");
        }

        asientoService.add(new AsientoEntity(null, fecha, orden, vinculo, null, null, null, null));
        List<CuentaMovimiento> lineas = new ArrayList<>();
        int item = 0;
        lineas.add(crearLinea(movimiento, conceptoLínea, proveedor.getNumeroCuenta(), debita,
                factura.total(), ++item));

        int ordenArticulo = 0;
        for (FacturaCompraArticulo facturaArticulo : articulos) {
            Articulo articulo = articuloService.getArticuloById(facturaArticulo.articuloId())
                    .orElseThrow(() -> new FacturaCompraException(Codigo.ARTICULO_INEXISTENTE,
                            "No existe el artículo " + facturaArticulo.articuloId()));
            if (articulo.getNumeroCuenta() == null) {
                throw new FacturaCompraException(Codigo.CUENTA_FALTANTE,
                        "El artículo " + articulo.getArticuloId() + " no tiene cuenta contable");
            }
            lineas.add(crearLinea(movimiento, conceptoLínea, articulo.getNumeroCuenta(),
                    (byte) (1 - debita), facturaArticulo.precioFinal(), ++item));

            ProveedorArticulo proveedorArticulo = new ProveedorArticulo();
            proveedorArticulo.setProveedorMovimientoId(movimiento.getProveedorMovimientoId());
            proveedorArticulo.setOrden(++ordenArticulo);
            proveedorArticulo.setArticuloId(facturaArticulo.articuloId());
            proveedorArticulo.setCantidad(facturaArticulo.cantidad() == null
                    ? BigDecimal.ZERO : facturaArticulo.cantidad());
            proveedorArticulo.setConcepto(facturaArticulo.concepto() == null
                    ? "" : facturaArticulo.concepto());
            proveedorArticulo.setPrecioUnitario(facturaArticulo.precioUnitario() == null
                    ? BigDecimal.ZERO : facturaArticulo.precioUnitario().multiply(factor));
            proveedorArticulo.setPrecioFinal(facturaArticulo.precioFinal().multiply(factor));
            proveedorArticulo.setEntregado(Byte.valueOf((byte) 1).equals(articulo.getDirecto())
                    ? proveedorArticulo.getCantidad() : BigDecimal.ZERO);
            proveedorArticulo.setAsignado(BigDecimal.ZERO);
            proveedorArticuloService.add(proveedorArticulo);
        }

        return resultado(movimiento, vinculo, lineas);
    }

    @Override
    public FacturaCompraDetalle consultar(Long proveedorMovimientoId) {
        ProveedorMovimiento movimiento = proveedorMovimientoService.findByProveedorMovimientoId(proveedorMovimientoId);
        List<ProveedorArticulo> articulos = proveedorArticuloService
                .findAllByProveedorMovimientoId(proveedorMovimientoId, false);
        List<CuentaMovimiento> lineas = movimiento.getFechaContable() == null
                || movimiento.getOrdenContable() == null
                ? List.of()
                : cuentaMovimientoService.findAllByAsiento(movimiento.getFechaContable(),
                        movimiento.getOrdenContable(), 0, 2).stream()
                        .filter(linea -> Objects.equals(linea.getComprobanteId(), movimiento.getComprobanteId()))
                        .toList();

        List<FacturaCompraArticuloDetalle> articulosDetalle = articulos.stream()
                .map(this::detalleArticulo)
                .toList();
        String comprobanteDescripcion = null;
        try {
            comprobanteDescripcion = comprobanteService.findByComprobanteId(movimiento.getComprobanteId())
                    .getDescripcion();
        } catch (RuntimeException ignored) {
            // Comprobante ausente: el detalle sigue siendo útil.
        }

        return new FacturaCompraDetalle(
                movimiento.getProveedorMovimientoId(),
                movimiento.getProveedorId(),
                movimiento.getNombreBeneficiario(),
                movimiento.getComprobanteId(),
                comprobanteDescripcion,
                movimiento.getPrefijo(),
                movimiento.getNumeroComprobante(),
                movimiento.getFechaComprobante(),
                movimiento.getFechaVencimiento(),
                movimiento.getNeto(),
                movimiento.getImporte(),
                movimiento.getConcepto(),
                movimiento.getGeograficaId(),
                movimiento.getFechaContable(),
                movimiento.getOrdenContable(),
                articulosDetalle,
                aLineas(lineas));
    }

    @Override
    public boolean existe(Integer proveedorId, Integer comprobanteId, Integer prefijo, Long numeroComprobante) {
        return proveedorMovimientoService.findByComprobante(proveedorId, comprobanteId, prefijo, numeroComprobante)
                .isPresent();
    }

    private CuentaMovimiento crearLinea(ProveedorMovimiento movimiento, String concepto, BigDecimal cuenta,
                                        byte debita, BigDecimal importe, int item) {
        return cuentaMovimientoService.createCuentaMovimiento(CuentaMovimiento.builder()
                .fechaContable(movimiento.getFechaContable())
                .ordenContable(movimiento.getOrdenContable())
                .item(item)
                .numeroCuenta(cuenta)
                .debita(debita)
                .comprobanteId(movimiento.getComprobanteId())
                .concepto(concepto)
                .importe(importe)
                .build());
    }

    private FacturaCompraArticuloDetalle detalleArticulo(ProveedorArticulo articulo) {
        String nombre = articulo.getArticulo() != null ? articulo.getArticulo().getNombre() : null;
        if (nombre == null && articulo.getArticuloId() != null) {
            nombre = articuloService.getArticuloById(articulo.getArticuloId())
                    .map(Articulo::getNombre)
                    .orElse(null);
        }
        return new FacturaCompraArticuloDetalle(
                articulo.getArticuloId(),
                nombre,
                articulo.getConcepto(),
                articulo.getCantidad(),
                articulo.getPrecioUnitario(),
                articulo.getPrecioFinal(),
                articulo.getEntregado(),
                articulo.getAsignado());
    }

    private FacturaCompraResultado resultado(ProveedorMovimiento movimiento, String vinculo,
                                             List<CuentaMovimiento> movimientos) {
        List<AsientoContableLinea> lineas = aLineas(movimientos);
        BigDecimal debe = total(lineas, (byte) 1);
        BigDecimal haber = total(lineas, (byte) 0);
        return new FacturaCompraResultado(
                movimiento.getProveedorMovimientoId(),
                movimiento.getFechaContable(),
                movimiento.getOrdenContable(),
                vinculo,
                debe,
                haber,
                debe.compareTo(haber) == 0,
                lineas);
    }

    private List<AsientoContableLinea> aLineas(List<CuentaMovimiento> movimientos) {
        Map<BigDecimal, String> nombres = nombres(movimientos);
        return movimientos.stream()
                .map(movimiento -> new AsientoContableLinea(
                        movimiento.getNumeroCuenta(),
                        nombre(movimiento, nombres),
                        movimiento.getDebita(),
                        movimiento.getImporte()))
                .toList();
    }

    private Map<BigDecimal, String> nombres(List<CuentaMovimiento> movimientos) {
        List<BigDecimal> cuentas = movimientos.stream()
                .map(CuentaMovimiento::getNumeroCuenta)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (cuentas.isEmpty()) {
            return Map.of();
        }
        return cuentaService.findAllByNumeroCuentaIn(cuentas).stream()
                .filter(cuenta -> cuenta.getNumeroCuenta() != null)
                .collect(Collectors.toMap(Cuenta::getNumeroCuenta,
                        cuenta -> cuenta.getNombre() == null ? "" : cuenta.getNombre(),
                        (primero, segundo) -> primero));
    }

    private static String nombre(CuentaMovimiento movimiento, Map<BigDecimal, String> nombres) {
        if (movimiento.getCuenta() != null && movimiento.getCuenta().getNombre() != null) {
            return movimiento.getCuenta().getNombre();
        }
        return nombres.get(movimiento.getNumeroCuenta());
    }

    private static BigDecimal total(List<AsientoContableLinea> lineas, byte debita) {
        return lineas.stream()
                .filter(linea -> linea.debita() != null && linea.debita() == debita)
                .map(AsientoContableLinea::importe)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static String formatear(Integer prefijo, Long numero) {
        return String.format("%04d-%08d", prefijo == null ? 0 : prefijo, numero == null ? 0L : numero);
    }

    private static OffsetDateTime normalizar(OffsetDateTime fecha) {
        return fecha.withOffsetSameInstant(ZoneOffset.UTC)
                .toLocalDate()
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);
    }

}
