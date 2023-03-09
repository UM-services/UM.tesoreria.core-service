/**
 *
 */
package ar.edu.um.tesoreria.rest.service;

import ar.edu.um.tesoreria.rest.exception.CuentaMovimientoException;
import ar.edu.um.tesoreria.rest.model.CuentaMovimiento;
import ar.edu.um.tesoreria.rest.model.view.CuentaMovimientoAsiento;
import ar.edu.um.tesoreria.rest.repository.ICuentaMovimientoRepository;
import ar.edu.um.tesoreria.rest.service.view.CuentaMovimientoAsientoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class CuentaMovimientoService {

    @Resource
    private ICuentaMovimientoRepository repository;

    @Resource
    private EntregaService entregaService;

    @Resource
    private CuentaMovimientoAsientoService cuentaMovimientoAsientoService;

    public List<CuentaMovimiento> findAllByAsiento(OffsetDateTime fechaContable, Integer ordenContable,
                                                   Integer itemDesde, Integer debita) {
        if (debita < 2) {
            return repository.findAllByFechaContableAndOrdenContableAndDebitaAndItemGreaterThanEqual(fechaContable,
                    ordenContable, debita.byteValue(), itemDesde,
                    Sort.by("debita").descending().and(Sort.by("item").ascending()));
        }
        return repository.findAllByFechaContableAndOrdenContableAndItemGreaterThanEqual(fechaContable, ordenContable,
                itemDesde, Sort.by("debita").descending().and(Sort.by("item").ascending()));
    }

    public List<CuentaMovimiento> findAllByNumeroCuentaAndFechaContableBetweenAndApertura(BigDecimal numeroCuenta,
                                                                                          OffsetDateTime desde, OffsetDateTime hasta, Byte apertura) {
        return repository.findAllByNumeroCuentaAndFechaContableBetweenAndApertura(numeroCuenta, desde, hasta, apertura,
                Sort.by("fechaContable").ascending().and(Sort.by("ordenContable").ascending()));
    }

    public List<CuentaMovimientoAsiento> findAllEntregaDetalleByProveedorMovimientoId(Long proveedorMovimientoId)
            throws JsonProcessingException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        List<String> asientos = entregaService.findAllDetalleByProveedorMovimientoId(proveedorMovimientoId, false)
                .stream()
                .map(e -> dateTimeFormatter.format(e.getFechaContable().plusDays(1)) + "." + e.getOrdenContable())
                .collect(Collectors.toList());
        log.debug("Asientos -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter()
                .writeValueAsString(asientos));
        List<CuentaMovimientoAsiento> cuentaMovimientoAsientos = cuentaMovimientoAsientoService
                .findAllByAsientoIn(asientos, Sort.by("fechaContable").ascending().and(Sort.by("ordenContable")
                        .ascending().and(Sort.by("debita").descending().and(Sort.by("item").ascending()))));
        log.debug("Asientos -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter()
                .writeValueAsString(cuentaMovimientoAsientos));
        return cuentaMovimientoAsientos;
    }

    public List<CuentaMovimientoAsiento> findAllEntregaDetalleByProveedorMovimientoIds(
            List<Long> proveedorMovimientoIds) throws JsonProcessingException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        List<String> asientos = entregaService.findAllDetalleByProveedorMovimientoIds(proveedorMovimientoIds, false)
                .stream()
                .map(e -> dateTimeFormatter.format(e.getFechaContable().plusDays(1)) + "." + e.getOrdenContable())
                .collect(Collectors.toList());
        log.debug("Asientos -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter()
                .writeValueAsString(asientos));
        List<CuentaMovimientoAsiento> cuentaMovimientoAsientos = cuentaMovimientoAsientoService
                .findAllByAsientoIn(asientos, Sort.by("fechaContable").ascending().and(Sort.by("ordenContable")
                        .ascending().and(Sort.by("debita").descending().and(Sort.by("item").ascending()))));
        log.debug("Asientos -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter()
                .writeValueAsString(cuentaMovimientoAsientos));
        return cuentaMovimientoAsientos;
    }

    public CuentaMovimiento findLastByFecha(OffsetDateTime fechaContable) {
        return repository.findTopByFechaContableOrderByOrdenContableDesc(fechaContable)
                .orElseGet(() -> new CuentaMovimiento());
    }

    public CuentaMovimiento findByCuentaMovimientoId(Long cuentaMovimientoId) {
        return repository.findByCuentaMovimientoId(cuentaMovimientoId)
                .orElseThrow(() -> new CuentaMovimientoException(cuentaMovimientoId));
    }

    public CuentaMovimiento add(CuentaMovimiento cuentaMovimiento) {
        cuentaMovimiento = repository.save(cuentaMovimiento);
        try {
            log.debug("CuentaMovimiento -> {}", JsonMapper.builder().findAndAddModules().build()
                    .writerWithDefaultPrettyPrinter().writeValueAsString(cuentaMovimiento));
        } catch (JsonProcessingException e) {
            log.debug("Error JSON Parser");
        }
        this.depurateAsiento(cuentaMovimiento.getFechaContable(), cuentaMovimiento.getOrdenContable());
        return cuentaMovimiento;
    }

    @Transactional
    public void deleteAsiento(OffsetDateTime fechaContable, Integer ordenContable) {
        repository.deleteAllByFechaContableAndOrdenContable(fechaContable, ordenContable);
    }

    @Transactional
    public void deleteByCuentaMovimientoId(Long cuentaMovimientoId) {
        repository.deleteByCuentaMovimientoId(cuentaMovimientoId);
    }

    @Transactional
    public void depurateAsiento(OffsetDateTime fechaContable, Integer ordenContable) {
        repository.deleteByFechaContableAndOrdenContableAndItem(fechaContable, ordenContable, 0);
    }

}
