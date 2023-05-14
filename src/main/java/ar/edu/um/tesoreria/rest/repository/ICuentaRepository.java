/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import ar.edu.um.tesoreria.rest.kotlin.model.Cuenta;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface ICuentaRepository extends JpaRepository<Cuenta, BigDecimal> {

	public List<Cuenta> findAllByGradoAndNumeroCuentaGreaterThan(Integer grado, BigDecimal numeroCuenta, Sort sort);

	public List<Cuenta> findAllByNumeroCuentaIn(List<BigDecimal> cuentas);

	public List<Cuenta> findAllByGradoAndNumeroCuentaBetween(Integer grado, BigDecimal desde, BigDecimal hasta);

	public Optional<Cuenta> findByNumeroCuenta(BigDecimal numeroCuenta);

	public Optional<Cuenta> findByCuentaContableId(Long cuentaContableId);

	@Modifying
	public void deleteByNumeroCuenta(BigDecimal numeroCuenta);

}
