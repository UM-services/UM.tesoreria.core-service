/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.OrdenPagoEntregado;

/**
 * @author daniel
 *
 */
@Repository
public interface IOrdenPagoEntregadoRepository extends JpaRepository<OrdenPagoEntregado, Long> {

	public Optional<OrdenPagoEntregado> findByOrdenPagoId(Long ordenPagoId);

}
