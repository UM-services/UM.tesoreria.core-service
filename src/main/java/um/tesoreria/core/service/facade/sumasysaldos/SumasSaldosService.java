package um.tesoreria.core.service.facade.sumasysaldos;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.hexagonal.contable.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.kotlin.model.Ejercicio;
import um.tesoreria.core.service.facade.ContabilidadService;
import um.tesoreria.core.service.facade.SaldosCuenta;
import um.tesoreria.core.service.facade.mayor.MayorConsultaException;
import um.tesoreria.core.service.facade.mayor.MayorConsultaException.Codigo;

/**
 * Balance de sumas y saldos en JSON para la pantalla. Capa legacy a propósito, como el mayor: las reglas de cálculo
 * viven acá y en {@link ContabilidadService#saldosPorCuenta}, no en el front.
 *
 * <p>Los saldos de todas las cuentas salen de tres consultas agrupadas (apertura, movimientos anteriores y período),
 * no de tres consultas por cuenta.
 */
@Service
@RequiredArgsConstructor
public class SumasSaldosService {

    private final ContabilidadService contabilidadService;
    private final CuentaService cuentaService;

    public SumasSaldosResponse consultar(SumasSaldosRequest request) {
        if (request == null || request.desde() == null || request.hasta() == null) {
            throw new MayorConsultaException(Codigo.FECHAS_REQUERIDAS, "Desde y hasta son obligatorios");
        }
        if (request.desde().isAfter(request.hasta())) {
            throw new MayorConsultaException(Codigo.DESDE_POSTERIOR, "La fecha desde no puede ser posterior a hasta");
        }
        Ejercicio ejercicio = contabilidadService.verifyEjercicio(request.desde(), request.hasta());
        if (ejercicio == null) {
            throw new MayorConsultaException(Codigo.FUERA_DE_EJERCICIO,
                    "Desde y hasta deben pertenecer al mismo ejercicio");
        }
        List<SumasSaldosFila> filas = filas(ejercicio, request.desde(), request.hasta());
        return new SumasSaldosResponse(request.desde(), request.hasta(), filas, totales(filas));
    }

    /** Una fila por cuenta de grado 5, en orden de número de cuenta; también las que no tienen movimientos. */
    public List<SumasSaldosFila> filas(Ejercicio ejercicio, OffsetDateTime desde, OffsetDateTime hasta) {
        contabilidadService.recalculateGrados();
        Map<BigDecimal, SaldosCuenta> saldos = contabilidadService.saldosPorCuenta(ejercicio, desde, hasta);
        List<Cuenta> cuentas = cuentaService.findAllGrado5();
        return cuentas.stream()
                .map(cuenta -> SumasSaldosFila.de(cuenta,
                        saldos.getOrDefault(ContabilidadService.claveCuenta(cuenta.getNumeroCuenta()),
                                SaldosCuenta.VACIO)))
                .toList();
    }

    private SumasSaldosResponse.Totales totales(List<SumasSaldosFila> filas) {
        return new SumasSaldosResponse.Totales(
                suma(filas, SumasSaldosFila::inicialDebitos), suma(filas, SumasSaldosFila::inicialCreditos),
                suma(filas, SumasSaldosFila::debitos), suma(filas, SumasSaldosFila::creditos),
                suma(filas, SumasSaldosFila::saldoDeudor), suma(filas, SumasSaldosFila::saldoAcreedor));
    }

    private BigDecimal suma(List<SumasSaldosFila> filas, java.util.function.Function<SumasSaldosFila, BigDecimal> campo) {
        return filas.stream().map(campo).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}