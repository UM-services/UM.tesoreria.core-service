/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.FacultadPagoCuenta;

/**
 * @author daniel
 *
 */
@Repository
public interface FacultadPagoCuentaRepository extends JpaRepository<FacultadPagoCuenta, Long> {

	public Optional<FacultadPagoCuenta> findByFacultadIdAndTipoPagoId(Integer facultadId, Integer tipoPagoId);

}
