package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Motor de asientos propuestos de una orden de pago a proveedor. Es dominio puro: ni Spring
 * ni persistencia. Replica {@code clsCtlOPago.makeAsientosOrdenPago} del legacy y lo usan el
 * preview, el alta y la reconstrucción de traza.
 *
 * <p>Reglas del legacy que se conservan:</p>
 * <ul>
 *   <li>La cuenta del proveedor y la de cada artículo salen del comprobante de la factura:
 *       proveedor con {@code debita} de la factura, artículos con {@code 1 - debita}.</li>
 *   <li>El asiento de costos usa el {@code debita} de la factura sobre la cuenta del artículo
 *       y {@code 1 - debita} sobre la cuenta de la ubicación, con {@code comprobanteId = 1}.</li>
 *   <li>Los valores usan el {@code debita} del comprobante de la orden de pago.</li>
 *   <li>Si un hijo todavía no tiene asiento, la fecha es la referencia (fecha del comprobante
 *       de la orden de pago) para facturas y entregas, y la fecha de emisión para los valores;
 *       el orden queda {@code null} para que el llamador lo reserve.</li>
 * </ul>
 */
public final class AsientoPropuestoBuilder {

    /** Comprobante que el legacy usa en las líneas de asignación de costos. */
    private static final Integer COMPROBANTE_COSTOS = 1;

    private AsientoPropuestoBuilder() {
    }

    /**
     * @param contexto      datos de la orden de pago y sus hijos
     * @param soloSinAsiento {@code true} para el alta: sólo propone asientos de hijos que todavía
     *                       no tienen asiento. {@code false} para el preview/reconstrucción:
     *                       propone también los que ya existen.
     */
    public static List<AsientoPropuesto> buildOrdenPago(OrdenPagoContexto contexto, boolean soloSinAsiento) {
        Map<String, Acumulador> asientos = new LinkedHashMap<>();
        contexto.facturas().forEach(factura -> agregarFactura(asientos, contexto, factura, soloSinAsiento));
        contexto.entregas().forEach(entrega -> agregarEntrega(asientos, contexto, entrega, soloSinAsiento));
        contexto.valores().forEach(valor -> agregarValor(asientos, contexto, valor, soloSinAsiento));
        return asientos.values().stream().map(Acumulador::toAsiento).toList();
    }

    /**
     * Asientos propuestos de una orden de pago directa (sin proveedor): un asiento de encabezado
     * con los gastos al debe y la obligación al haber, más un asiento por cada valor.
     *
     * <p>Replica {@code clsCtlOPago.makeAsientosDirecta}: si un valor heredó la fecha y el orden
     * del encabezado, el legacy lo fuerza a un asiento nuevo en la fecha de emisión.</p>
     */
    public static List<AsientoPropuesto> buildDirecta(OrdenPagoDirectaContexto contexto, boolean soloSinAsiento) {
        Map<String, Acumulador> asientos = new LinkedHashMap<>();
        agregarEncabezado(asientos, contexto, soloSinAsiento);
        contexto.valores().forEach(valor -> agregarValorDirecta(asientos, contexto, valor, soloSinAsiento));
        return asientos.values().stream().map(Acumulador::toAsiento).toList();
    }

    private static void agregarEncabezado(Map<String, Acumulador> asientos, OrdenPagoDirectaContexto contexto,
                                          boolean soloSinAsiento) {
        if (soloSinAsiento && !sinOrden(contexto.ordenContableOrdenPago())) {
            return;
        }
        String concepto = contexto.conceptoPago();
        List<LineaPropuesta> lineas = new ArrayList<>();
        for (ContextoGasto gasto : contexto.gastos()) {
            lineas.add(new LineaPropuesta(gasto.numeroCuenta(), (byte) 1, valorAbsoluto(gasto.importe()),
                    COMPROBANTE_COSTOS, null, concepto));
        }
        lineas.add(new LineaPropuesta(contexto.numeroCuentaObligacion(), (byte) 0,
                valorAbsoluto(contexto.importe()), COMPROBANTE_COSTOS, null, concepto));
        OffsetDateTime fecha = sinOrden(contexto.ordenContableOrdenPago())
                || contexto.fechaContableOrdenPago() == null
                ? contexto.fechaComprobante() : contexto.fechaContableOrdenPago();
        agregar(asientos, fecha, contexto.ordenContableOrdenPago(),
                new OrigenAsiento(OrigenAsiento.Tipo.ENCABEZADO, contexto.proveedorMovimientoId()), lineas);
    }

    private static void agregarValorDirecta(Map<String, Acumulador> asientos, OrdenPagoDirectaContexto contexto,
                                            ContextoValor valor, boolean soloSinAsiento) {
        if (soloSinAsiento && !sinOrden(valor.ordenContable())) {
            return;
        }
        byte debita = debita(contexto.debita());
        String concepto = contexto.conceptoPago();
        BigDecimal importe = valorAbsoluto(valor.importe());
        List<LineaPropuesta> lineas = List.of(
                new LineaPropuesta(contexto.numeroCuentaObligacion(), debita, importe,
                        COMPROBANTE_COSTOS, null, concepto),
                new LineaPropuesta(valor.numeroCuentaContrapartida(), (byte) (1 - debita), importe,
                        COMPROBANTE_COSTOS, null, concepto));
        boolean mismoAsiento = !sinOrden(valor.ordenContable())
                && valor.ordenContable().equals(contexto.ordenContableOrdenPago())
                && valor.fechaContable() != null && contexto.fechaContableOrdenPago() != null
                && valor.fechaContable().toLocalDate().isEqual(contexto.fechaContableOrdenPago().toLocalDate());
        Integer orden = mismoAsiento ? null : valor.ordenContable();
        OffsetDateTime fecha = mismoAsiento || sinOrden(orden) || valor.fechaContable() == null
                ? valor.fechaEmision() : valor.fechaContable();
        agregar(asientos, fecha, orden,
                new OrigenAsiento(OrigenAsiento.Tipo.VALOR, referenciaValor(valor)), lineas);
    }

    /**
     * La reconstrucción necesita el id real del valor para poder corregir su asiento; el alta,
     * que crea valores nuevos, usa la posición porque el id todavía no existe.
     */
    private static Long referenciaValor(ContextoValor valor) {
        return valor.valorMovimientoId() != null
                ? valor.valorMovimientoId()
                : (valor.posicion() == null ? null : valor.posicion().longValue());
    }

    private static void agregarFactura(Map<String, Acumulador> asientos, OrdenPagoContexto contexto,
                                       ContextoFactura factura, boolean soloSinAsiento) {
        if (soloSinAsiento && !sinOrden(factura.ordenContable())) {
            return;
        }
        byte debita = debita(factura.debita());
        String concepto = contexto.conceptoPago();
        List<LineaPropuesta> lineas = new ArrayList<>();
        lineas.add(new LineaPropuesta(factura.numeroCuentaProveedor(), debita, valorAbsoluto(factura.importe()),
                factura.comprobanteId(), factura.proveedorId(), concepto));
        for (ContextoArticulo articulo : factura.articulos()) {
            BigDecimal importe = sinImporte(articulo.precioFinal())
                    ? valorAbsoluto(factura.importe())
                    : valorAbsoluto(articulo.precioFinal());
            lineas.add(new LineaPropuesta(articulo.numeroCuenta(), (byte) (1 - debita), importe,
                    factura.comprobanteId(), factura.proveedorId(), concepto));
        }
        agregar(asientos, fechaDe(factura.fechaContable(), factura.ordenContable(), contexto),
                factura.ordenContable(), new OrigenAsiento(OrigenAsiento.Tipo.FACTURA, factura.proveedorMovimientoId()),
                lineas);
    }

    private static void agregarEntrega(Map<String, Acumulador> asientos, OrdenPagoContexto contexto,
                                       ContextoEntrega entrega, boolean soloSinAsiento) {
        if (soloSinAsiento && !sinOrden(entrega.ordenContable())) {
            return;
        }
        byte debita = debita(entrega.debita());
        List<LineaPropuesta> lineas = new ArrayList<>();
        for (ContextoEntregaDetalle detalle : entrega.detalles()) {
            String concepto = detalle.sinCuentaUbicacion()
                    ? nombreCompuesto(detalle)
                    : contexto.conceptoPago() + " - Asignacion de Costos";
            BigDecimal importe = valorAbsoluto(detalle.cantidad());
            lineas.add(new LineaPropuesta(detalle.numeroCuentaArticulo(), debita, importe,
                    COMPROBANTE_COSTOS, entrega.proveedorId(), concepto));
            lineas.add(new LineaPropuesta(detalle.numeroCuentaUbicacion(), (byte) (1 - debita), importe,
                    COMPROBANTE_COSTOS, entrega.proveedorId(), concepto));
        }
        agregar(asientos, fechaDe(entrega.fechaContable(), entrega.ordenContable(), contexto),
                entrega.ordenContable(), new OrigenAsiento(OrigenAsiento.Tipo.ENTREGA, entrega.entregaId()),
                lineas);
    }

    private static void agregarValor(Map<String, Acumulador> asientos, OrdenPagoContexto contexto,
                                     ContextoValor valor, boolean soloSinAsiento) {
        if (soloSinAsiento && !sinOrden(valor.ordenContable())) {
            return;
        }
        byte debita = debita(contexto.debita());
        String concepto = contexto.conceptoPago();
        BigDecimal importe = valorAbsoluto(valor.importe());
        List<LineaPropuesta> lineas = List.of(
                new LineaPropuesta(contexto.numeroCuentaProveedor(), debita, importe,
                        contexto.comprobanteId(), contexto.proveedorId(), concepto),
                new LineaPropuesta(valor.numeroCuentaContrapartida(), (byte) (1 - debita), importe,
                        contexto.comprobanteId(), contexto.proveedorId(), concepto));
        OffsetDateTime fecha = fechaDe(valor.fechaContable(), valor.ordenContable(), valor.fechaEmision());
        agregar(asientos, fecha, valor.ordenContable(),
                new OrigenAsiento(OrigenAsiento.Tipo.VALOR, referenciaValor(valor)), lineas);
    }

    private static void agregar(Map<String, Acumulador> asientos, OffsetDateTime fecha, Integer orden,
                                OrigenAsiento origen, List<LineaPropuesta> lineas) {
        if (lineas.isEmpty()) {
            return;
        }
        asientos.computeIfAbsent(clave(fecha, orden, origen), key -> new Acumulador(fecha, orden))
                .add(origen, lineas);
    }

    /**
     * Un hijo sin asiento genera su propio asiento: no se agrupa con otros hijos de la misma
     * fecha, porque cada uno recibe un orden reservado distinto.
     */
    private static String clave(OffsetDateTime fecha, Integer orden, OrigenAsiento origen) {
        if (sinOrden(orden)) {
            return "N:" + origen.tipo() + ":" + origen.referenciaId();
        }
        return "E:" + fecha.toLocalDate() + ":" + orden;
    }

    private static OffsetDateTime fechaDe(OffsetDateTime fechaContable, Integer ordenContable,
                                          OrdenPagoContexto contexto) {
        return sinOrden(ordenContable) || fechaContable == null ? contexto.fechaComprobante() : fechaContable;
    }

    private static OffsetDateTime fechaDe(OffsetDateTime fechaContable, Integer ordenContable,
                                          OffsetDateTime fechaEmision) {
        return sinOrden(ordenContable) || fechaContable == null ? fechaEmision : fechaContable;
    }

    private static boolean sinOrden(Integer orden) {
        return orden == null || orden == 0;
    }

    private static boolean sinImporte(BigDecimal importe) {
        return importe == null || importe.signum() == 0;
    }

    private static BigDecimal valorAbsoluto(BigDecimal importe) {
        return importe == null ? BigDecimal.ZERO : importe.abs();
    }

    private static byte debita(Byte debita) {
        return debita == null ? 0 : debita;
    }

    private static String nombreCompuesto(ContextoEntregaDetalle detalle) {
        return nulo(detalle.nombreUbicacion()) + "/" + nulo(detalle.nombreArticulo());
    }

    private static String nulo(String texto) {
        return texto == null ? "" : texto;
    }

    private static final class Acumulador {

        private final OffsetDateTime fecha;
        private final Integer orden;
        private final List<OrigenAsiento> origenes = new ArrayList<>();
        private final List<LineaPropuesta> lineas = new ArrayList<>();

        private Acumulador(OffsetDateTime fecha, Integer orden) {
            this.fecha = fecha;
            this.orden = orden;
        }

        private void add(OrigenAsiento origen, List<LineaPropuesta> nuevas) {
            if (!origenes.contains(origen)) {
                origenes.add(origen);
            }
            lineas.addAll(nuevas);
        }

        private AsientoPropuesto toAsiento() {
            return new AsientoPropuesto(fecha, orden == null || orden == 0 ? null : orden,
                    List.copyOf(origenes), List.copyOf(lineas));
        }
    }
}
