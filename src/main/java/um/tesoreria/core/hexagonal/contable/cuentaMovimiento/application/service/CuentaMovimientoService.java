package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.exception.CuentaMovimientoException;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.*;
import um.tesoreria.core.model.view.CuentaMovimientoAsiento;
import um.tesoreria.core.service.EntregaService;
import um.tesoreria.core.service.view.CuentaMovimientoAsientoService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CuentaMovimientoService {

    private final CreateCuentaMovimientoUseCase createCuentaMovimientoUseCase;
    private final UpdateCuentaMovimientoUseCase updateCuentaMovimientoUseCase;
    private final FindByCuentaMovimientoIdUseCase findByCuentaMovimientoIdUseCase;
    private final FindAllByNumeroCuentaUseCase findAllByNumeroCuentaUseCase;
    private final FindAllByAsientoUseCase findAllByAsientoUseCase;
    private final FindAllByNumeroCuentaAndFechaContableBetweenAndAperturaUseCase findAllByNumeroCuentaAndFechaContableBetweenAndAperturaUseCase;
    private final FindLastByFechaUseCase findLastByFechaUseCase;
    private final DeleteByCuentaMovimientoIdUseCase deleteByCuentaMovimientoIdUseCase;
    private final DeleteAllByCuentaMovimientoIdInUseCase deleteAllByCuentaMovimientoIdInUseCase;
    private final DeleteAsientoUseCase deleteAsientoUseCase;
    private final EntregaService entregaService;
    private final CuentaMovimientoAsientoService cuentaMovimientoAsientoService;

    public CuentaMovimiento createCuentaMovimiento(CuentaMovimiento cuentaMovimiento) {
        return createCuentaMovimientoUseCase.createCuentaMovimiento(cuentaMovimiento);
    }

    public CuentaMovimiento updateCuentaMovimiento(CuentaMovimiento newCuentaMovimiento, Long cuentaMovimientoId) {
        return updateCuentaMovimientoUseCase.updateCuentaMovimiento(newCuentaMovimiento, cuentaMovimientoId);
    }

    public CuentaMovimiento findByCuentaMovimientoId(Long cuentaMovimientoId) {
        return findByCuentaMovimientoIdUseCase.findByCuentaMovimientoId(cuentaMovimientoId)
                .orElseThrow(() -> new CuentaMovimientoException(cuentaMovimientoId));
    }

    public List<CuentaMovimiento> findAllByNumeroCuenta(BigDecimal numeroCuenta, Boolean onlyOne) {
        return findAllByNumeroCuentaUseCase.findAllByNumeroCuenta(numeroCuenta, onlyOne);
    }

    public List<CuentaMovimiento> findAllByAsiento(OffsetDateTime fechaContable, Integer ordenContable, Integer itemDesde, Integer debita) {
        return findAllByAsientoUseCase.findAllByAsiento(fechaContable, ordenContable, itemDesde, debita);
    }

    public List<CuentaMovimiento> findAllByNumeroCuentaAndFechaContableBetweenAndApertura(BigDecimal numeroCuenta, OffsetDateTime desde, OffsetDateTime hasta, Byte apertura) {
        return findAllByNumeroCuentaAndFechaContableBetweenAndAperturaUseCase.findAllByNumeroCuentaAndFechaContableBetweenAndApertura(numeroCuenta, desde, hasta, apertura);
    }

    public CuentaMovimiento findLastByFecha(OffsetDateTime fechaContable) {
        log.debug("Processing CuentaMovimientoService.findLastByFecha -> {}", fechaContable);
        return findLastByFechaUseCase.findLastByFecha(fechaContable);
    }

    @Transactional
    public void deleteAsiento(OffsetDateTime fechaContable, Integer ordenContable) {
        deleteAsientoUseCase.deleteAsiento(fechaContable, ordenContable);
    }

    @Transactional
    public void deleteByCuentaMovimientoId(Long cuentaMovimientoId) {
        deleteByCuentaMovimientoIdUseCase.deleteByCuentaMovimientoId(cuentaMovimientoId);
    }

    @Transactional
    public void deleteAllByCuentaMovimientoIdIn(List<Long> cuentaMovimientoIds) {
        log.debug("Processing deleteAllByCuentaMovimientoIdIn");
        deleteAllByCuentaMovimientoIdInUseCase.deleteAllByCuentaMovimientoIdIn(cuentaMovimientoIds);
    }

    private List<CuentaMovimientoAsiento> getCuentaMovimientoAsientos(List<String> asientos) {
        return cuentaMovimientoAsientoService.findAllByAsientoIn(asientos,
                org.springframework.data.domain.Sort.by("fechaContable").ascending()
                        .and(org.springframework.data.domain.Sort.by("ordenContable").ascending()
                                .and(org.springframework.data.domain.Sort.by("debita").descending()
                                        .and(org.springframework.data.domain.Sort.by("item").ascending()))));
    }

    public List<CuentaMovimientoAsiento> findAllEntregaDetalleByProveedorMovimientoId(Long proveedorMovimientoId) throws com.fasterxml.jackson.core.JsonProcessingException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        List<String> asientos = entregaService.findAllDetalleByProveedorMovimientoId(proveedorMovimientoId, false).stream()
                .map(e -> dateTimeFormatter.format(e.getFechaContable()) + "." + e.getOrdenContable())
                .collect(Collectors.toList());
        return getCuentaMovimientoAsientos(asientos);
    }

    public List<CuentaMovimientoAsiento> findAllEntregaDetalleByProveedorMovimientoIds(List<Long> proveedorMovimientoIds) throws com.fasterxml.jackson.core.JsonProcessingException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        List<String> asientos = entregaService.findAllDetalleByProveedorMovimientoIds(proveedorMovimientoIds, false).stream()
                .map(e -> dateTimeFormatter.format(e.getFechaContable()) + "." + e.getOrdenContable())
                .collect(Collectors.toList());
        return getCuentaMovimientoAsientos(asientos);
    }
}
