/**
 * 
 */
package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.persistence.entity.CuentaMovimientoEntity;

/**
 * @author daniel
 *
 */
public interface JpaCuentaMovimientoRepository extends JpaRepository<CuentaMovimientoEntity, Long> {

	List<CuentaMovimientoEntity> findAllByNumeroCuenta(BigDecimal numeroCuenta);

	List<CuentaMovimientoEntity> findAllByNumeroCuentaAndFechaContableBetweenAndApertura(BigDecimal numeroCuenta,
	                                                                                     OffsetDateTime desde, OffsetDateTime hasta, Byte apertura, Sort sort);

	List<CuentaMovimientoEntity> findAllByFechaContableAndOrdenContableAndItemGreaterThanEqual(
			OffsetDateTime fechaContable, Integer ordenContable, Integer item, Sort sort);

	List<CuentaMovimientoEntity> findAllByFechaContableAndOrdenContableAndDebitaAndItemGreaterThanEqual(
			OffsetDateTime fechaContable, Integer ordenContable, Byte debita, Integer item, Sort sort);

	Optional<CuentaMovimientoEntity> findTopByNumeroCuenta(BigDecimal numeroCuenta);

	Optional<CuentaMovimientoEntity> findTopByFechaContableOrderByOrdenContableDesc(OffsetDateTime fechaContable);

	Optional<CuentaMovimientoEntity> findByCuentaMovimientoId(Long cuentaMovimientoId);

	@Modifying
	void deleteAllByFechaContableAndOrdenContable(OffsetDateTime fechaContable, Integer ordenContable);

	@Modifying
	void deleteByCuentaMovimientoId(Long cuentaMovimientoId);

	@Modifying
    void deleteAllByCuentaMovimientoIdIn(List<Long> cuentaMovimientoIds);

}
