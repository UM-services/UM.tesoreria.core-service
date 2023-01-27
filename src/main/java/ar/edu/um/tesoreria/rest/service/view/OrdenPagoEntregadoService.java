/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.view.OrdenPagoEntregadoException;
import ar.edu.um.tesoreria.rest.model.view.OrdenPagoEntregado;
import ar.edu.um.tesoreria.rest.repository.view.IOrdenPagoEntregadoRepository;

/**
 * @author daniel
 *
 */
@Service
public class OrdenPagoEntregadoService {

	@Autowired
	private IOrdenPagoEntregadoRepository repository;

	public OrdenPagoEntregado findByOrdenPagoId(Long ordenPagoId) {
		return repository.findByOrdenPagoId(ordenPagoId)
				.orElseThrow(() -> new OrdenPagoEntregadoException(ordenPagoId));
	}

}
