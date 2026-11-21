package um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asiento.application.exception.AsientoException;
import um.tesoreria.core.hexagonal.contable.asiento.application.service.AsientoService;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoLinea;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoRegistro;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoResumen;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Arma el resumen del asiento (totales y líneas) a partir de {@code movcon} y {@code asiento}. */
@Component
@RequiredArgsConstructor
public class AsientoIngresoResumenBuilder {

    private static final int COMPROBANTE_INGRESO = 3;

    private final CuentaMovimientoService cuentaMovimientoService;
    private final AsientoService asientoService;
    private final CuentaService cuentaService;
    private final AsientoIngresoCatalogo catalogo;

    public AsientoIngresoResumen build(OffsetDateTime fecha, Integer tipoPagoId, AsientoIngresoRegistro registro) {
        String tipoPagoNombre = catalogo.tipoPagoNombre(tipoPagoId);
        if (registro == null) {
            return AsientoIngresoResumen.builder()
                    .fecha(fecha)
                    .tipoPagoId(tipoPagoId)
                    .tipoPagoNombre(tipoPagoNombre)
                    .existe(false)
                    .totalDebe(BigDecimal.ZERO)
                    .totalHaber(BigDecimal.ZERO)
                    .cuadra(true)
                    .lineas(List.of())
                    .build();
        }

        Integer orden = registro.getOrdenContable();
        List<AsientoIngresoLinea> lineas = cuentaMovimientoService.findAllByAsiento(fecha, orden, 0, 2).stream()
                .filter(movimiento -> movimiento.getComprobanteId() != null
                        && movimiento.getComprobanteId() == COMPROBANTE_INGRESO)
                .map(movimiento -> AsientoIngresoLinea.builder()
                        .numeroCuenta(movimiento.getNumeroCuenta())
                        .nombreCuenta(movimiento.getCuenta() == null ? null : movimiento.getCuenta().getNombre())
                        .debita(movimiento.getDebita())
                        .importe(movimiento.getImporte() == null ? BigDecimal.ZERO : movimiento.getImporte())
                        .build())
                .toList();

        completarNombres(lineas);

        BigDecimal totalDebe = total(lineas, (byte) 1);
        BigDecimal totalHaber = total(lineas, (byte) 0);

        String vinculo = null;
        try {
            vinculo = asientoService.findByAsiento(fecha, orden).getVinculo();
        } catch (AsientoException e) {
            // El mapa puede existir sin cabecera (asiento sin movimientos): el legacy muestra totales en cero.
        }

        return AsientoIngresoResumen.builder()
                .fecha(fecha)
                .tipoPagoId(tipoPagoId)
                .tipoPagoNombre(tipoPagoNombre)
                .existe(true)
                .ordenContable(orden)
                .vinculo(vinculo)
                .totalDebe(totalDebe)
                .totalHaber(totalHaber)
                .cuadra(totalDebe.compareTo(totalHaber) == 0)
                .lineas(lineas)
                .build();
    }

    /**
     * Las líneas recién insertadas pueden venir de una entidad gestionada sin la asociación
     * {@code cuenta} inicializada; se completan los nombres por lote desde {@code plancta}.
     */
    private void completarNombres(List<AsientoIngresoLinea> lineas) {
        List<BigDecimal> sinNombre = lineas.stream()
                .filter(linea -> linea.getNombreCuenta() == null && linea.getNumeroCuenta() != null)
                .map(AsientoIngresoLinea::getNumeroCuenta)
                .distinct()
                .toList();
        if (sinNombre.isEmpty()) {
            return;
        }
        Map<BigDecimal, String> nombres = cuentaService.findAllByNumeroCuentaIn(sinNombre).stream()
                .filter(cuenta -> cuenta.getNumeroCuenta() != null)
                .collect(Collectors.toMap(Cuenta::getNumeroCuenta,
                        cuenta -> cuenta.getNombre() == null ? "" : cuenta.getNombre(),
                        (primero, segundo) -> primero));
        lineas.stream()
                .filter(linea -> linea.getNombreCuenta() == null && linea.getNumeroCuenta() != null)
                .forEach(linea -> linea.setNombreCuenta(nombres.get(linea.getNumeroCuenta())));
    }

    private static BigDecimal total(List<AsientoIngresoLinea> lineas, byte debita) {
        return lineas.stream()
                .filter(linea -> linea.getDebita() != null && linea.getDebita() == debita)
                .map(AsientoIngresoLinea::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
