package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.LineaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.SumaCuenta;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface CuentaMovimientoRepository {
    List<CuentaMovimiento> findAllByNumeroCuenta(BigDecimal numeroCuenta);
    Optional<CuentaMovimiento> findTopByNumeroCuenta(BigDecimal numeroCuenta);
    List<CuentaMovimiento> findAllByFechaContableAndOrdenContableAndItemGreaterThanEqual(OffsetDateTime fechaContable, Integer ordenContable, Integer item);
    List<CuentaMovimiento> findAllByFechaContableAndOrdenContableAndDebitaAndItemGreaterThanEqual(OffsetDateTime fechaContable, Integer ordenContable, Byte debita, Integer item);
    List<CuentaMovimiento> findAllByProveedorMovimientoId(Long proveedorMovimientoId);
    List<CuentaMovimiento> findAllByNumeroCuentaAndFechaContableBetweenAndApertura(BigDecimal numeroCuenta, OffsetDateTime desde, OffsetDateTime hasta, Byte apertura);
    List<LineaMovimiento> findLineasByFechaContable(OffsetDateTime desde, OffsetDateTime hasta, int limite);
    long countByFechaContable(OffsetDateTime desde, OffsetDateTime hasta);
    List<SumaCuenta> sumarPorCuenta(OffsetDateTime desde, OffsetDateTime hasta, Byte apertura);
    Optional<CuentaMovimiento> findTopByFechaContableOrderByOrdenContableDesc(OffsetDateTime fechaContable);
    boolean existsByFechaContableBetween(OffsetDateTime desde, OffsetDateTime hasta);
    Optional<CuentaMovimiento> findByCuentaMovimientoId(Long cuentaMovimientoId);
    CuentaMovimiento save(CuentaMovimiento cuentaMovimiento);
    void deleteAllByFechaContableAndOrdenContable(OffsetDateTime fechaContable, Integer ordenContable);
    void deleteByCuentaMovimientoId(Long cuentaMovimientoId);
    void deleteAllByCuentaMovimientoIdIn(List<Long> cuentaMovimientoIds);
}