/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.TipoPagoFecha;
import ar.edu.um.tesoreria.rest.model.view.pk.TipoPagoFechaPk;

/**
 * @author daniel
 *
 */
@Repository
public interface ITipoPagoFechaRepository extends JpaRepository<TipoPagoFecha, TipoPagoFechaPk> {

	public List<TipoPagoFecha> findAllByFechaAcreditacion(OffsetDateTime fechaAcreditacion);

}
