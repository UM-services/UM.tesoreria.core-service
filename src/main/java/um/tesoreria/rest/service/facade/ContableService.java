/**
 *
 */
package um.tesoreria.rest.service.facade;

import um.tesoreria.rest.exception.AsientoException;
import um.tesoreria.rest.exception.CuentaMovimientoException;
import um.tesoreria.rest.exception.EjercicioBloqueadoException;
import um.tesoreria.rest.exception.EjercicioException;
import um.tesoreria.rest.exception.facade.ContableException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import um.tesoreria.rest.kotlin.model.*;
import um.tesoreria.rest.kotlin.model.internal.AsientoInternal;
import um.tesoreria.rest.service.*;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private ComprobanteService comprobanteService;

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
        return new AsientoInternal(fechaContable, ordenContable, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Transactional
    public Boolean deleteAsiento(OffsetDateTime fechaContable, Integer ordenContable) throws EjercicioBloqueadoException {
        return deleteAsientoDesde(fechaContable, ordenContable, 0);
    }

    @Transactional
    public Boolean deleteAsientoDesde(OffsetDateTime fechaContable, Integer ordenContable, Integer item) {

        try {
            log.debug("FechaContable={}", fechaContable);
            Ejercicio ejercicio = ejercicioService.findByFecha(fechaContable);
            try {
                log.debug("Ejercicio={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(ejercicio));
            } catch (JsonProcessingException e) {
                log.debug("Sin Ejercicio");
            }
            if (ejercicio.getBloqueado() == 1) {
                log.debug("Ejercicio bloqueado");
                throw new EjercicioBloqueadoException(fechaContable);
            }

            Asiento asiento = null;
            try {
                asiento = asientoService.findByAsiento(fechaContable, ordenContable);
            } catch (AsientoException e) {

            }
            if (asiento != null) {
                asientoService.deleteByAsientoId(asiento.getAsientoId());
            }

            for (CuentaMovimiento cuentaMovimiento : cuentaMovimientoService.findAllByAsiento(fechaContable, ordenContable, item, 2)) {
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

    @Transactional
    public void contraAsiento(OffsetDateTime fechaContable, Integer ordenContable, AsientoInternal asientoInternalContra) {
        if (tieneContraAsiento(fechaContable, ordenContable)) {
            throw new ContableException(fechaContable, ordenContable);
        }

        Comprobante comprobante = comprobanteService.findByTipoTransaccionId(99);

        if (comprobante == null) {
            throw new ContableException(fechaContable, ordenContable);
        }

        List<CuentaMovimiento> cuentaMovimientos = cuentaMovimientoService.findAllByAsiento(fechaContable, ordenContable, 0, 2);
        AsientoInternal mem = nextAsiento(asientoInternalContra.getFechaContable(), null);
        asientoInternalContra.setOrdenContable(mem.getOrdenContable());

        Asiento asiento = null;
        try {
            asiento = asientoService.findByAsiento(fechaContable, ordenContable);
            asiento.setFechaContra(asientoInternalContra.getFechaContable());
            asiento.setOrdenContra(asientoInternalContra.getOrdenContable());
            asiento = asientoService.update(asiento, asiento.getAsientoId());
        } catch (AsientoException e) {
            asiento = new Asiento(null, fechaContable, ordenContable, "contraasiento", asientoInternalContra.getFechaContable(), asientoInternalContra.getOrdenContable(), null, null);
            asiento = asientoService.add(asiento);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String vinculo = MessageFormat.format("Contraasiento de ASIENTO {0} - {1}", fechaContable.format(formatter), ordenContable);
        asiento = new Asiento(null, asientoInternalContra.getFechaContable(), asientoInternalContra.getOrdenContable(), vinculo, null, null, null, null);
        asiento = asientoService.add(asiento);

        for (CuentaMovimiento cuentaMovimiento : cuentaMovimientos) {
            cuentaMovimiento.setCuentaMovimientoId(null);
            cuentaMovimiento.setFechaContable(asientoInternalContra.getFechaContable());
            cuentaMovimiento.setOrdenContable(asientoInternalContra.getOrdenContable());
            cuentaMovimiento.setDebita((byte) (1 - cuentaMovimiento.getDebita()));
            cuentaMovimiento = cuentaMovimientoService.add(cuentaMovimiento);
        }
    }

    private Boolean tieneContraAsiento(OffsetDateTime fechaContable, Integer ordenContable) {
        try {
            Asiento asiento = asientoService.findByAsiento(fechaContable, ordenContable);
            if (asiento.getFechaContra() != null) {
                return true;
            }
        } catch (AsientoException e) {
        }
        return false;
    }

    @Transactional
    public void saveAsiento(OffsetDateTime fechaContable, Integer ordenContable, Long proveedorMovimientoId, String vinculo, List<CuentaMovimiento> cuentaMovimientos) {
        Ejercicio ejercicio = ejercicioService.findByFecha(fechaContable);
        if (ejercicio.getBloqueado() == 1) {
            throw new EjercicioBloqueadoException(fechaContable);
        }
        Integer item = 0;

        Asiento asiento = new Asiento(null, fechaContable, ordenContable, vinculo, null, null, null, null);
        try {
            log.debug("Asiento={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(asiento));
        } catch (JsonProcessingException e) {
            log.debug("Asiento=JsonException");
        }
        asiento = asientoService.add(asiento);
        try {
            log.debug("Asiento={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(asiento));
        } catch (JsonProcessingException e) {
            log.debug("Asiento=JsonException");
        }

        for (CuentaMovimiento cuentaMovimiento : cuentaMovimientos) {
            try {
                log.debug("CuentaMovimiento={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(cuentaMovimiento));
            } catch (JsonProcessingException e) {
                log.debug("CuentaMovimiento=JsonException");
            }
            cuentaMovimiento.setFechaContable(fechaContable);
            cuentaMovimiento.setOrdenContable(ordenContable);
            cuentaMovimiento.setItem(++item);
            cuentaMovimiento.setProveedorMovimientoId(proveedorMovimientoId);
            try {
                log.debug("CuentaMovimiento={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(cuentaMovimiento));
            } catch (JsonProcessingException e) {
                log.debug("CuentaMovimiento=JsonException");
            }
            cuentaMovimiento = cuentaMovimientoService.add(cuentaMovimiento);
            try {
                log.debug("CuentaMovimiento={}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(cuentaMovimiento));
            } catch (JsonProcessingException e) {
                log.debug("CuentaMovimiento=JsonException");
            }
        }
        deleteAsientoDesde(fechaContable, ordenContable, item + 1);

    }
}
