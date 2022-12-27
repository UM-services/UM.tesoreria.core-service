/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.CuentaMovimientoAsiento;

/**
 * @author daniel
 *
 */
@Repository
public interface ICuentaMovimientoAsientoRepository extends JpaRepository<CuentaMovimientoAsiento, Long> {

	public List<CuentaMovimientoAsiento> findAllByAsientoIn(List<String> asientos, Sort sort);

}
