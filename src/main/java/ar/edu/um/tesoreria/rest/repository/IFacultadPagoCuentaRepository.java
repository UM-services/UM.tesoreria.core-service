/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.FacultadPagoCuenta;

/**
 * @author daniel
 *
 */
@Repository
public interface IFacultadPagoCuentaRepository extends JpaRepository<FacultadPagoCuenta, Long> {

	public Optional<FacultadPagoCuenta> findByFacultadIdAndTipoPagoId(Integer facultadId, Integer tipoPagoId);

}
