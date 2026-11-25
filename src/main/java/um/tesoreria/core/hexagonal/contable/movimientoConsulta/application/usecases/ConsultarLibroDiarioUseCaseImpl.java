package um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.proveedor.application.service.ProveedorService;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.ProveedorNombre;
import um.tesoreria.core.hexagonal.comprobante.application.service.ComprobanteService;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.LineaMovimiento;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.exception.MovimientoConsultaException;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.exception.MovimientoConsultaException.Codigo;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.TopeMovimientos;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientoDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LibroDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LineaDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.RangoContable;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.ports.in.ConsultarLibroDiarioUseCase;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Libro Diario: todas las líneas de {@code movcon} de un rango de días, agrupadas por asiento (fecha y orden).
 * <pre>
 * validar fechas -> más días que el tope: 409 -> contar líneas -> más que el tope: 409
 *                -> leer como mucho tope + 1 líneas (proyección, una consulta) -> más que el tope: 409
 *                   (conteo y lectura en la misma transacción de solo lectura)
 *                -> nombres: cuentas, comprobantes y proveedores (una consulta por fuente, más las relaciones
 *                   de carga inmediata de cuenta y comprobante: unas pocas sentencias por pedido)
 *                -> agrupar por asiento y sumar
 * </pre>
 */
@Component
@RequiredArgsConstructor
public class ConsultarLibroDiarioUseCaseImpl implements ConsultarLibroDiarioUseCase {

    private final CuentaMovimientoService cuentaMovimientoService;
    private final CuentaService cuentaService;
    private final ComprobanteService comprobanteService;
    private final ProveedorService proveedorService;
    private final TopeMovimientos topeMovimientos;

    @Override
    @Transactional(readOnly = true)
    public LibroDiario consultarLibroDiario(OffsetDateTime desde, OffsetDateTime hasta) {
        if (desde == null || hasta == null) {
            throw new MovimientoConsultaException(Codigo.FECHAS_REQUERIDAS, "Desde y hasta son obligatorios");
        }
        RangoContable rango = RangoContable.deFechas(desde, hasta);
        if (rango.fueraDeRango()) {
            throw new MovimientoConsultaException(Codigo.FECHA_FUERA_DE_RANGO,
                    "Las fechas deben estar entre " + RangoContable.MINIMO + " y " + RangoContable.MAXIMO);
        }
        if (rango.invertido()) {
            throw new MovimientoConsultaException(Codigo.DESDE_POSTERIOR,
                    "La fecha desde no puede ser posterior a hasta");
        }
        if (rango.dias() > topeMovimientos.maximoDias()) {
            throw new MovimientoConsultaException(Codigo.RANGO_DEMASIADO_LARGO,
                    "El rango tiene " + rango.dias() + " días y el máximo es " + topeMovimientos.maximoDias()
                            + ": acorte el rango");
        }
        int maximo = topeMovimientos.maximo();
        long cantidad = cuentaMovimientoService.countByFechaContable(rango.inicio(), rango.fin());
        if (cantidad > maximo) {
            throw demasiados(String.valueOf(cantidad), maximo);
        }

        // Otra transacción pudo agregar líneas después del conteo: se lee una más que el tope para detectarlo.
        List<LineaMovimiento> lineas = cuentaMovimientoService.findLineasByFechaContable(rango.inicio(), rango.fin(),
                maximo + 1);
        if (lineas.size() > maximo) {
            throw demasiados("más de " + maximo, maximo);
        }
        Nombres nombres = nombres(lineas);

        // Las líneas llegan ordenadas por fecha y orden: el mapa ordenado conserva ese orden de asientos.
        List<AsientoDiario> asientos = lineas.stream()
                .map(linea -> armarLinea(linea, nombres))
                .collect(Collectors.groupingBy(l -> List.of(l.fechaContable(), l.ordenContable()),
                        LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .map(ConsultarLibroDiarioUseCaseImpl::cerrar)
                .toList();
        BigDecimal totalDebe = asientos.stream().map(AsientoDiario::totalDebe).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalHaber = asientos.stream().map(AsientoDiario::totalHaber).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new LibroDiario(desde, hasta, asientos, totalDebe, totalHaber);
    }

    private static MovimientoConsultaException demasiados(String cantidad, int maximo) {
        return new MovimientoConsultaException(Codigo.DEMASIADOS_MOVIMIENTOS,
                "El rango tiene " + cantidad + " movimientos y el máximo es " + maximo + ": acorte el rango");
    }

    private Nombres nombres(List<LineaMovimiento> lineas) {
        if (lineas.isEmpty()) {
            return new Nombres(Map.of(), Map.of(), Map.of());
        }
        List<BigDecimal> numeros = lineas.stream()
                .map(LineaMovimiento::numeroCuenta)
                .filter(Objects::nonNull)
                .map(BigDecimal::stripTrailingZeros)
                .distinct()
                .toList();
        Map<BigDecimal, String> cuentas = new HashMap<>();
        for (Cuenta cuenta : cuentaService.findAllByNumeroCuentaIn(numeros)) {
            cuentas.putIfAbsent(cuenta.getNumeroCuenta().stripTrailingZeros(), cuenta.getNombre());
        }

        Map<Integer, String> comprobantes = new HashMap<>();
        for (Comprobante comprobante : comprobanteService.findAll()) {
            comprobantes.putIfAbsent(comprobante.getComprobanteId(), comprobante.getDescripcion());
        }

        Set<Integer> proveedorIds = lineas.stream()
                .map(LineaMovimiento::proveedorId)
                .filter(id -> id != null && id != 0)
                .collect(Collectors.toSet());
        Map<Integer, String> proveedores = new HashMap<>();
        for (ProveedorNombre proveedor : proveedorService.getNombres(proveedorIds)) {
            proveedores.putIfAbsent(proveedor.proveedorId(), proveedor.razonSocial());
        }
        return new Nombres(cuentas, comprobantes, proveedores);
    }

    private static LineaDiario armarLinea(LineaMovimiento linea, Nombres nombres) {
        BigDecimal importe = linea.importe() == null ? BigDecimal.ZERO : linea.importe();
        boolean debita = linea.debita() != null && linea.debita() == 1;
        String nombreCuenta = linea.numeroCuenta() == null ? null
                : nombres.cuentas().get(linea.numeroCuenta().stripTrailingZeros());
        return new LineaDiario(
                linea.fechaContable(),
                linea.ordenContable(),
                linea.item(),
                linea.numeroCuenta(),
                nombreCuenta,
                linea.comprobanteId() == null ? null : nombres.comprobantes().get(linea.comprobanteId()),
                linea.proveedorId() == null ? null : nombres.proveedores().get(linea.proveedorId()),
                linea.concepto(),
                debita ? importe : null,
                debita ? null : importe);
    }


    private static AsientoDiario cerrar(List<LineaDiario> lineas) {
        BigDecimal debe = BigDecimal.ZERO;
        BigDecimal haber = BigDecimal.ZERO;
        for (LineaDiario linea : lineas) {
            debe = sumar(debe, linea.debe());
            haber = sumar(haber, linea.haber());
        }
        LineaDiario primera = lineas.getFirst();
        return new AsientoDiario(primera.fechaContable(), primera.ordenContable(), List.copyOf(lineas), debe, haber);
    }

    private static BigDecimal sumar(BigDecimal total, BigDecimal importe) {
        return importe == null ? total : total.add(importe);
    }

    private record Nombres(Map<BigDecimal, String> cuentas, Map<Integer, String> comprobantes,
                           Map<Integer, String> proveedores) {
    }
}
