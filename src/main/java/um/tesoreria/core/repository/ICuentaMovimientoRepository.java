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
public interface ICuentaMovimientoRepository extends JpaRepository<CuentaMovimiento, Long> {

	public List<CuentaMovimiento> findAllByNumeroCuentaAndFechaContableBetweenAndApertura(BigDecimal numeroCuenta,
																						  OffsetDateTime desde, OffsetDateTime hasta, Byte apertura, Sort sort);

	public List<CuentaMovimiento> findAllByFechaContableAndOrdenContableAndItemGreaterThanEqual(
			OffsetDateTime fechaContable, Integer ordenContable, Integer item, Sort sort);

	public List<CuentaMovimiento> findAllByFechaContableAndOrdenContableAndDebitaAndItemGreaterThanEqual(
			OffsetDateTime fechaContable, Integer ordenContable, Byte debita, Integer item, Sort sort);

	public Optional<CuentaMovimiento> findTopByFechaContableOrderByOrdenContableDesc(OffsetDateTime fechaContable);

	public Optional<CuentaMovimiento> findByCuentaMovimientoId(Long cuentaMovimientoId);

	@Modifying
	public void deleteAllByFechaContableAndOrdenContable(OffsetDateTime fechaContable, Integer ordenContable);

	@Modifying
	public void deleteByCuentaMovimientoId(Long cuentaMovimientoId);

}
