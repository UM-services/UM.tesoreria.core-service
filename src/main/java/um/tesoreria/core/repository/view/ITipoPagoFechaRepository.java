/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.TipoPagoFecha;
import um.tesoreria.core.model.view.pk.TipoPagoFechaPk;

/**
 * @author daniel
 *
 */
@Repository
public interface ITipoPagoFechaRepository extends JpaRepository<TipoPagoFecha, TipoPagoFechaPk> {

	public List<TipoPagoFecha> findAllByFechaAcreditacion(OffsetDateTime fechaAcreditacion);

}
