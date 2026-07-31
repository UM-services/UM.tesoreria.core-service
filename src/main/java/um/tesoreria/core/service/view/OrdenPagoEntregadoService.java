/**
 * 
 */
package um.tesoreria.core.service.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.view.OrdenPagoEntregadoException;
import um.tesoreria.core.model.view.OrdenPagoEntregado;
import um.tesoreria.core.repository.view.OrdenPagoEntregadoRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class OrdenPagoEntregadoService {

	private final OrdenPagoEntregadoRepository repository;

	public OrdenPagoEntregado findByOrdenPagoId(Long ordenPagoId) {
		return repository.findByOrdenPagoId(ordenPagoId)
				.orElseThrow(() -> new OrdenPagoEntregadoException(ordenPagoId));
	}

}
