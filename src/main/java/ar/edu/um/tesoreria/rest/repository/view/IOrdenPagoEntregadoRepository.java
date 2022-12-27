/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.OrdenPagoEntregado;

/**
 * @author daniel
 *
 */
@Repository
public interface IOrdenPagoEntregadoRepository extends JpaRepository<OrdenPagoEntregado, Long> {

	public Optional<OrdenPagoEntregado> findByOrdenPagoId(Long ordenPagoId);

}
