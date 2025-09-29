/**
 *
 */
package um.tesoreria.core.service;

import um.tesoreria.core.exception.CuentaMovimientoException;
import um.tesoreria.core.kotlin.model.CuentaMovimiento;
import um.tesoreria.core.model.view.CuentaMovimientoAsiento;
import um.tesoreria.core.repository.CuentaMovimientoRepository;
import um.tesoreria.core.service.view.CuentaMovimientoAsientoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author daniel
 */
@Service
@Slf4j
public class CuentaMovimientoService {

    private final CuentaMovimientoRepository repository;
    private final EntregaService entregaService;
    private final CuentaMovimientoAsientoService cuentaMovimientoAsientoService;

    public CuentaMovimientoService(CuentaMovimientoRepository repository, EntregaService entregaService, CuentaMovimientoAsientoService cuentaMovimientoAsientoService) {
        this.repository = repository;
        this.entregaService = entregaService;
        this.cuentaMovimientoAsientoService = cuentaMovimientoAsientoService;
    }

    public List<CuentaMovimiento> findAllByNumeroCuenta(BigDecimal numeroCuenta, Boolean onlyOne) {
        if (onlyOne) {
            var cuenta = repository.findTopByNumeroCuenta(numeroCuenta);
            return cuenta.map(Collections::singletonList).orElse(Collections.emptyList());
        }
        return repository.findAllByNumeroCuenta(numeroCuenta);
    }

    public List<CuentaMovimiento> findAllByAsiento(OffsetDateTime fechaContable, Integer ordenContable, Integer itemDesde, Integer debita) {
        if (debita < 2) {
            return repository.findAllByFechaContableAndOrdenContableAndDebitaAndItemGreaterThanEqual(fechaContable, ordenContable, debita.byteValue(), itemDesde, Sort.by("debita").descending().and(Sort.by("item").ascending()));
        }
        return repository.findAllByFechaContableAndOrdenContableAndItemGreaterThanEqual(fechaContable, ordenContable, itemDesde, Sort.by("debita").descending().and(Sort.by("item").ascending()));
    }

    public List<CuentaMovimiento> findAllByNumeroCuentaAndFechaContableBetweenAndApertura(BigDecimal numeroCuenta, OffsetDateTime desde, OffsetDateTime hasta, Byte apertura) {
        return repository.findAllByNumeroCuentaAndFechaContableBetweenAndApertura(numeroCuenta, desde, hasta, apertura, Sort.by("fechaContable").ascending().and(Sort.by("ordenContable").ascending()));
    }

    private List<CuentaMovimientoAsiento> getCuentaMovimientoAsientos(List<String> asientos) throws JsonProcessingException {
        List<CuentaMovimientoAsiento> cuentaMovimientoAsientos = cuentaMovimientoAsientoService.findAllByAsientoIn(asientos, Sort.by("fechaContable").ascending().and(Sort.by("ordenContable").ascending().and(Sort.by("debita").descending().and(Sort.by("item").ascending()))));
        log.debug("Asientos -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(cuentaMovimientoAsientos));
        return cuentaMovimientoAsientos;
    }

    public List<CuentaMovimientoAsiento> findAllEntregaDetalleByProveedorMovimientoId(Long proveedorMovimientoId) throws JsonProcessingException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        List<String> asientos = entregaService.findAllDetalleByProveedorMovimientoId(proveedorMovimientoId, false).stream().map(e -> dateTimeFormatter.format(e.getFechaContable()) + "." + e.getOrdenContable()).collect(Collectors.toList());
        return getCuentaMovimientoAsientos(asientos);
    }

    public List<CuentaMovimientoAsiento> findAllEntregaDetalleByProveedorMovimientoIds(List<Long> proveedorMovimientoIds) throws JsonProcessingException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        List<String> asientos = entregaService.findAllDetalleByProveedorMovimientoIds(proveedorMovimientoIds, false).stream().map(e -> dateTimeFormatter.format(e.getFechaContable()) + "." + e.getOrdenContable()).collect(Collectors.toList());
        return getCuentaMovimientoAsientos(asientos);
    }

    public CuentaMovimiento findLastByFecha(OffsetDateTime fechaContable) {
        log.debug("Processing CuentaMovimientoService.findLastByFecha -> {}", fechaContable);
        var movimiento = repository.findTopByFechaContableOrderByOrdenContableDesc(fechaContable).orElseGet(CuentaMovimiento::new);
        log.debug("Last CuentaMovimiento -> {}", movimiento.jsonify());
        return movimiento;
    }

    public CuentaMovimiento findByCuentaMovimientoId(Long cuentaMovimientoId) {
        return repository.findByCuentaMovimientoId(cuentaMovimientoId).orElseThrow(() -> new CuentaMovimientoException(cuentaMovimientoId));
    }

    public CuentaMovimiento add(CuentaMovimiento cuentaMovimiento) {
        return repository.save(cuentaMovimiento);
    }

    public CuentaMovimiento update(CuentaMovimiento newCuentaMovimiento, Long cuentaMovimientoId) {
        return repository.findByCuentaMovimientoId(cuentaMovimientoId).map(cuentaMovimiento -> {
            cuentaMovimiento = new CuentaMovimiento.Builder()
                    .cuentaMovimientoId(cuentaMovimientoId)
                    .fechaContable(newCuentaMovimiento.getFechaContable())
                    .ordenContable(newCuentaMovimiento.getOrdenContable())
                    .item(newCuentaMovimiento.getItem())
                    .numeroCuenta(newCuentaMovimiento.getNumeroCuenta())
                    .debita(newCuentaMovimiento.getDebita())
                    .comprobanteId(newCuentaMovimiento.getComprobanteId())
                    .concepto(newCuentaMovimiento.getConcepto())
                    .importe(newCuentaMovimiento.getImporte())
                    .proveedorId(newCuentaMovimiento.getProveedorId())
                    .numeroAnulado(newCuentaMovimiento.getNumeroAnulado())
                    .version(newCuentaMovimiento.getVersion())
                    .proveedorMovimientoId(newCuentaMovimiento.getProveedorMovimientoId())
                    .proveedorMovimientoIdOrdenPago(newCuentaMovimiento.getProveedorMovimientoIdOrdenPago())
                    .apertura(newCuentaMovimiento.getApertura())
                    .trackId(newCuentaMovimiento.getTrackId())
                    .build();
            return repository.save(cuentaMovimiento);
        }).orElseThrow(() -> new CuentaMovimientoException(cuentaMovimientoId));
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
    public void deleteAllByCuentaMovimientoIdIn(List<Long> cuentaMovimientoIds) {
        log.debug("Processing deleteAllByCuentaMovimientoIdIn");
        repository.deleteAllByCuentaMovimientoIdIn(cuentaMovimientoIds);
    }

}
