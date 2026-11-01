/**
 * 
 */
package um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.persistence.entity.CuentaEntity;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaCuentaRepository extends JpaRepository<CuentaEntity, BigDecimal> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<CuentaEntity> findWithLockByNumeroCuenta(BigDecimal numeroCuenta);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<CuentaEntity> findAllByNombreContainingOrderByNumeroCuenta(String fragmento);

    boolean existsByNumeroCuentaGreaterThanAndNumeroCuentaLessThanEqual(BigDecimal desde, BigDecimal hasta);

    List<CuentaEntity> findAllByGeograficaIdAndGradoOrderByNumeroCuenta(Integer geograficaId, Integer grado);

	List<CuentaEntity> findAllByGradoAndNumeroCuentaGreaterThan(Integer grado, BigDecimal numeroCuenta, Sort sort);

	List<CuentaEntity> findAllByNumeroCuentaIn(List<BigDecimal> cuentas);

	List<CuentaEntity> findAllByGradoAndNumeroCuentaBetween(Integer grado, BigDecimal desde, BigDecimal hasta);

	Optional<CuentaEntity> findByNumeroCuenta(BigDecimal numeroCuenta);

	Optional<CuentaEntity> findByCuentaContableId(Long cuentaContableId);

	void deleteByNumeroCuenta(BigDecimal numeroCuenta);

}
