/**
 * 
 */
package um.tesoreria.core.hexagonal.cuenta.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.cuenta.infrastructure.persistence.entity.CuentaEntity;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaCuentaRepository extends JpaRepository<CuentaEntity, BigDecimal> {

	List<CuentaEntity> findAllByGradoAndNumeroCuentaGreaterThan(Integer grado, BigDecimal numeroCuenta, Sort sort);

	List<CuentaEntity> findAllByNumeroCuentaIn(List<BigDecimal> cuentas);

	List<CuentaEntity> findAllByGradoAndNumeroCuentaBetween(Integer grado, BigDecimal desde, BigDecimal hasta);

	Optional<CuentaEntity> findByNumeroCuenta(BigDecimal numeroCuenta);

	Optional<CuentaEntity> findByCuentaContableId(Long cuentaContableId);

	@Modifying
	void deleteByNumeroCuenta(BigDecimal numeroCuenta);

}
