/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.CuentaMovimientoAsiento;

/**
 * @author daniel
 *
 */
@Repository
public interface ICuentaMovimientoAsientoRepository extends JpaRepository<CuentaMovimientoAsiento, Long> {

	public List<CuentaMovimientoAsiento> findAllByAsientoIn(List<String> asientos, Sort sort);

}
