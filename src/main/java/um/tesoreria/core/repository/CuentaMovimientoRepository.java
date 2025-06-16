/**
 * 
 */
package um.tesoreria.core.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import um.tesoreria.core.kotlin.model.CuentaMovimiento;

/**
 * @author daniel
 *
 */
public interface CuentaMovimientoRepository extends JpaRepository<CuentaMovimiento, Long> {

	List<CuentaMovimiento> findAllByNumeroCuenta(BigDecimal numeroCuenta);

	List<CuentaMovimiento> findAllByNumeroCuentaAndFechaContableBetweenAndApertura(BigDecimal numeroCuenta,
																						  OffsetDateTime desde, OffsetDateTime hasta, Byte apertura, Sort sort);

	List<CuentaMovimiento> findAllByFechaContableAndOrdenContableAndItemGreaterThanEqual(
			OffsetDateTime fechaContable, Integer ordenContable, Integer item, Sort sort);

	List<CuentaMovimiento> findAllByFechaContableAndOrdenContableAndDebitaAndItemGreaterThanEqual(
			OffsetDateTime fechaContable, Integer ordenContable, Byte debita, Integer item, Sort sort);

	Optional<CuentaMovimiento> findTopByNumeroCuenta(BigDecimal numeroCuenta);

	Optional<CuentaMovimiento> findTopByFechaContableOrderByOrdenContableDesc(OffsetDateTime fechaContable);

	Optional<CuentaMovimiento> findByCuentaMovimientoId(Long cuentaMovimientoId);

	@Modifying
	void deleteAllByFechaContableAndOrdenContable(OffsetDateTime fechaContable, Integer ordenContable);

	@Modifying
	void deleteByCuentaMovimientoId(Long cuentaMovimientoId);

	@Modifying
    void deleteAllByCuentaMovimientoIdIn(List<Long> cuentaMovimientoIds);

}
