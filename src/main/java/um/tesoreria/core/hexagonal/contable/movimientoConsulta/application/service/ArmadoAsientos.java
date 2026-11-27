package um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedor.application.service.ProveedorService;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.ProveedorNombre;
import um.tesoreria.core.hexagonal.comprobante.application.service.ComprobanteService;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.LineaMovimiento;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.AsientoDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LineaDiario;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model.LineaMovimientoContable;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Arma asientos a partir de líneas de {@code movcon}: resuelve nombres de cuenta, comprobante y proveedor (una
 * consulta por fuente, más las relaciones de carga inmediata de cuenta y comprobante) y suma cada lado.
 */
@Component
@RequiredArgsConstructor
public class ArmadoAsientos {

    private final CuentaService cuentaService;
    private final ComprobanteService comprobanteService;
    private final ProveedorService proveedorService;

    /** Un asiento por fecha y orden, en el orden en que llegan las líneas. */
    public List<AsientoDiario> asientos(List<LineaMovimiento> lineas) {
        Nombres nombres = nombres(lineas);
        return lineas.stream()
                .map(linea -> armarLinea(linea, nombres))
                .collect(Collectors.groupingBy(l -> List.of(l.fechaContable(), l.ordenContable()),
                        LinkedHashMap::new, Collectors.toList()))
                .values().stream()
                .map(ArmadoAsientos::cerrar)
                .toList();
    }

    /** Todas las líneas como un único asiento; {@code lineas} no puede estar vacía. */
    public AsientoDiario asiento(List<LineaMovimiento> lineas) {
        Nombres nombres = nombres(lineas);
        return cerrar(lineas.stream().map(linea -> armarLinea(linea, nombres)).toList());
    }

    /** Cada línea por separado, en el orden en que llegan, con nombres y CUIT del proveedor. */
    public List<LineaMovimientoContable> movimientos(List<LineaMovimiento> lineas) {
        Nombres nombres = nombres(lineas);
        return lineas.stream().map(linea -> {
            LineaDiario diario = armarLinea(linea, nombres);
            ProveedorNombre proveedor = nombres.proveedor(linea.proveedorId());
            return new LineaMovimientoContable(diario.fechaContable(), diario.ordenContable(), diario.item(),
                    diario.numeroCuenta(), diario.nombreCuenta(), diario.comprobante(),
                    linea.proveedorId() == null || linea.proveedorId() == 0 ? null : linea.proveedorId(),
                    diario.proveedor(),
                    proveedor == null ? null : proveedor.cuit(), diario.concepto(), diario.debe(), diario.haber());
        }).toList();
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
        Map<Integer, ProveedorNombre> proveedores = new HashMap<>();
        for (ProveedorNombre proveedor : proveedorService.getNombres(proveedorIds)) {
            proveedores.putIfAbsent(proveedor.proveedorId(), proveedor);
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
                nombres.razonSocial(linea.proveedorId()),
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
                           Map<Integer, ProveedorNombre> proveedores) {

        ProveedorNombre proveedor(Integer proveedorId) {
            return proveedorId == null ? null : proveedores.get(proveedorId);
        }

        String razonSocial(Integer proveedorId) {
            ProveedorNombre proveedor = proveedor(proveedorId);
            return proveedor == null ? null : proveedor.razonSocial();
        }
    }
}
