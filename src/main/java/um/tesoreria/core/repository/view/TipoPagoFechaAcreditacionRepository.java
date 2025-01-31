/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.TipoPagoFechaAcreditacion;

/**
 * @author daniel
 *
 */
@Repository
public interface TipoPagoFechaAcreditacionRepository extends JpaRepository<TipoPagoFechaAcreditacion, String> {

	List<TipoPagoFechaAcreditacion> findAllByFechaAcreditacion(OffsetDateTime fechaAcreditacion);

}
