/**
 *
 */
package ar.edu.um.tesoreria.rest.service.facade;

import ar.edu.um.tesoreria.rest.exception.AsientoException;
import ar.edu.um.tesoreria.rest.exception.CuentaMovimientoException;
import ar.edu.um.tesoreria.rest.exception.EjercicioBloqueadoException;
import ar.edu.um.tesoreria.rest.exception.EjercicioException;
import ar.edu.um.tesoreria.rest.model.Asiento;
import ar.edu.um.tesoreria.rest.model.Cuenta;
import ar.edu.um.tesoreria.rest.model.CuentaMovimiento;
import ar.edu.um.tesoreria.rest.model.Ejercicio;
import ar.edu.um.tesoreria.rest.model.kotlin.internal.AsientoInternal;
import ar.edu.um.tesoreria.rest.service.AsientoService;
import ar.edu.um.tesoreria.rest.service.CuentaMovimientoService;
import ar.edu.um.tesoreria.rest.service.CuentaService;
import ar.edu.um.tesoreria.rest.service.EjercicioService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

/**
 * @author daniel
 */
@Service
@Slf4j
public class ContableService {

    @Autowired
    private AsientoService asientoService;

    @Autowired
    private CuentaService cuentaService;

    @Autowired
    private EjercicioService ejercicioService;

    @Autowired
    private CuentaMovimientoService cuentaMovimientoService;

    public static Boolean isSaldoDeudor(BigDecimal cuenta) {
        List<Integer> digitos = List.of(1, 4);
        return digitos.contains(cuenta.divide(new BigDecimal("10000000000.0")).intValue());
    }

    public static Boolean isSaldoAcreedor(BigDecimal cuenta) {
        return !isSaldoDeudor(cuenta);
    }

    public void recalculateGrados() {
        List<Cuenta> cuentas = cuentaService.findAll();
        for (Cuenta cuenta : cuentas) {
            cuenta.setNombre(cuenta.getNombre().replace("\r\n", ""));
            cuenta.setGrado(5);
            cuenta.setGrado1(cuenta.getNumeroCuenta().divideToIntegralValue(new BigDecimal("10000000000.0")).multiply(new BigDecimal("10000000000.0")));
            cuenta.setGrado2(cuenta.getNumeroCuenta().divideToIntegralValue(new BigDecimal("100000000.0")).multiply(new BigDecimal("100000000.0")));
            cuenta.setGrado3(cuenta.getNumeroCuenta().divideToIntegralValue(new BigDecimal("1000000.0")).multiply(new BigDecimal("1000000.0")));
            cuenta.setGrado4(cuenta.getNumeroCuenta().divideToIntegralValue(new BigDecimal("10000.0")).multiply(new BigDecimal("10000.0")));
            if (cuenta.getNumeroCuenta().compareTo(cuenta.getGrado4()) == 0) cuenta.setGrado(4);
            if (cuenta.getNumeroCuenta().compareTo(cuenta.getGrado3()) == 0) cuenta.setGrado(3);
            if (cuenta.getNumeroCuenta().compareTo(cuenta.getGrado2()) == 0) cuenta.setGrado(2);
            if (cuenta.getNumeroCuenta().compareTo(cuenta.getGrado1()) == 0) cuenta.setGrado(1);
        }
        cuentas = cuentaService.saveAll(cuentas);
    }

    public Ejercicio verifyEjercicio(OffsetDateTime desde, OffsetDateTime hasta) {
        log.debug("Desde -> {} - Hasta -> {}", desde, hasta);
        Ejercicio ejercicio = null;
        try {
            ejercicio = ejercicioService.findByFecha(desde);
            log.debug("Ejercicio -> {}", ejercicio);
            OffsetDateTime fechaInicio = ejercicio.getFechaInicio().withOffsetSameInstant(ZoneOffset.UTC);
            OffsetDateTime fechaFinal = ejercicio.getFechaFinal().withOffsetSameInstant(ZoneOffset.UTC);
            if (desde.isBefore(fechaInicio)) {
                ejercicio = null;
            }
            if (hasta.isAfter(fechaFinal)) {
                ejercicio = null;
            }
        } catch (EjercicioException e) {
        }
        return ejercicio;
    }

    public List<BigDecimal> saldoInicial(BigDecimal cuenta, Ejercicio ejercicio, OffsetDateTime desde) {
        OffsetDateTime ejercicioInicio = ejercicio.getFechaInicio().withOffsetSameInstant(ZoneOffset.UTC);
        // Calcula saldo de asiento de apertura
        List<BigDecimal> saldos = saldoPeriodo(cuenta, ejercicioInicio, ejercicioInicio, (byte) 1);

        if (desde.isEqual(ejercicioInicio)) {
            return Arrays.asList(saldos.get(0), saldos.get(1));
        }
        // Suma los movimientos hasta el día anterior a desde
        BigDecimal saldoDeudor = saldos.get(0);
        BigDecimal saldoAcreedor = saldos.get(1);
        saldos = saldoPeriodo(cuenta, ejercicioInicio, desde.minusDays(1), (byte) 0);
        saldoDeudor = saldoDeudor.add(saldos.get(0));
        saldoAcreedor = saldoAcreedor.add(saldos.get(1));
        return Arrays.asList(saldoDeudor, saldoAcreedor);
    }

    public List<BigDecimal> saldoPeriodo(BigDecimal numeroCuenta, OffsetDateTime desde, OffsetDateTime hasta, Byte apertura) {
        List<CuentaMovimiento> saldos = cuentaMovimientoService.findAllByNumeroCuentaAndFechaContableBetweenAndApertura(numeroCuenta, desde, hasta, apertura);
        BigDecimal saldoDeudor = saldos.stream().filter(total -> total.getDebita() == 1).map(total -> total.getImporte()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saldoAcreedor = saldos.stream().filter(total -> total.getDebita() == 0).map(total -> total.getImporte()).reduce(BigDecimal.ZERO, BigDecimal::add);
        return Arrays.asList(saldoDeudor, saldoAcreedor);
    }

    public AsientoInternal nextAsiento(OffsetDateTime fechaContable, List<AsientoInternal> asientos) {
        CuentaMovimiento cuentaMovimiento = cuentaMovimientoService.findLastByFecha(fechaContable);
        Integer ordenContable = 1 + cuentaMovimiento.getOrdenContable();
        if (asientos != null) {
            for (AsientoInternal asiento : asientos) {
                if (asiento.getFechaContable().isEqual(fechaContable)) {
                    if (asiento.getOrdenContable() == ordenContable) {
                        ordenContable += 1;
                    }
                }
            }
        }
        cuentaMovimientoService.add(new CuentaMovimiento(null, fechaContable, ordenContable, 0, null, (byte) 0, null, "", BigDecimal.ZERO, null, 0, 0, null, null, (byte) 0, null, null, null, null, null, null, null));
        return new AsientoInternal(fechaContable, ordenContable, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Transactional
    public Boolean deleteAsiento(OffsetDateTime fechaContable, Integer ordenContable) throws EjercicioBloqueadoException {
        return deleteAsientoDesde(fechaContable, ordenContable, 0);
    }

    @Transactional
    public Boolean deleteAsientoDesde(OffsetDateTime fechaContable, Integer ordenContable, Integer item) {

        try {
            Ejercicio ejercicio = ejercicioService.findByFecha(fechaContable);
            if (ejercicio.getBloqueado() == 1) {
                throw new EjercicioBloqueadoException(fechaContable);
            }

            Asiento asiento = asientoService.findByAsiento(fechaContable, ordenContable);
            if (asiento != null) {
                asientoService.deleteByAsientoId(asiento.getAsientoId());
            }

            for (CuentaMovimiento cuentaMovimiento : cuentaMovimientoService.findAllByAsiento(fechaContable, ordenContable, 0, 2)) {
                cuentaMovimientoService.deleteByCuentaMovimientoId(cuentaMovimiento.getCuentaMovimientoId());
            }
        } catch (AsientoException e) {
            log.debug("Error Asiento {}", e.getMessage());
            return false;
        } catch (CuentaMovimientoException e) {
            log.debug("Error CuentaMovimiento {}", e.getMessage());
            return false;
        } catch (EjercicioBloqueadoException e) {
            log.debug("Error Ejercicio {}", e.getMessage());
            return false;
        }

        return true;
    }

}
