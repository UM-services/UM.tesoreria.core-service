/**
 * 
 */
package um.tesoreria.core.service.view;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.TipoPagoFecha;
import um.tesoreria.core.repository.view.ITipoPagoFechaRepository;

/**
 * @author daniel
 *
 */
@Service
public class TipoPagoFechaService {

	@Autowired
	private ITipoPagoFechaRepository repository;

	public List<TipoPagoFecha> findAllByFechaAcreditacion(OffsetDateTime fechaAcreditacion) {
		return repository.findAllByFechaAcreditacion(fechaAcreditacion);
	}

}
