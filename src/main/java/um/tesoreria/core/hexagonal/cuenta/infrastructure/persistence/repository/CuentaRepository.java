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
public interface CuentaRepository extends JpaRepository<CuentaEntity, BigDecimal> {

	public List<CuentaEntity> findAllByGradoAndNumeroCuentaGreaterThan(Integer grado, BigDecimal numeroCuenta, Sort sort);

	public List<CuentaEntity> findAllByNumeroCuentaIn(List<BigDecimal> cuentas);

	public List<CuentaEntity> findAllByGradoAndNumeroCuentaBetween(Integer grado, BigDecimal desde, BigDecimal hasta);

	public Optional<CuentaEntity> findByNumeroCuenta(BigDecimal numeroCuenta);

	public Optional<CuentaEntity> findByCuentaContableId(Long cuentaContableId);

	@Modifying
	public void deleteByNumeroCuenta(BigDecimal numeroCuenta);

}
