/**
 * 
 */
package um.tesoreria.core.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.Cuenta;

/**
 * @author daniel
 *
 */
@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, BigDecimal> {

	public List<Cuenta> findAllByGradoAndNumeroCuentaGreaterThan(Integer grado, BigDecimal numeroCuenta, Sort sort);

	public List<Cuenta> findAllByNumeroCuentaIn(List<BigDecimal> cuentas);

	public List<Cuenta> findAllByGradoAndNumeroCuentaBetween(Integer grado, BigDecimal desde, BigDecimal hasta);

	public Optional<Cuenta> findByNumeroCuenta(BigDecimal numeroCuenta);

	public Optional<Cuenta> findByCuentaContableId(Long cuentaContableId);

	@Modifying
	public void deleteByNumeroCuenta(BigDecimal numeroCuenta);

}
