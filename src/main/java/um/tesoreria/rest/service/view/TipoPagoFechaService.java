/**
 * 
 */
package um.tesoreria.rest.service.view;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.model.view.TipoPagoFecha;
import um.tesoreria.rest.repository.view.ITipoPagoFechaRepository;
import um.tesoreria.rest.repository.view.ITipoPagoFechaRepository;

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
