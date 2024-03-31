/**
 * 
 */
package um.tesoreria.core.service.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.view.OrdenPagoEntregadoException;
import um.tesoreria.core.model.view.OrdenPagoEntregado;
import um.tesoreria.core.repository.view.IOrdenPagoEntregadoRepository;

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
