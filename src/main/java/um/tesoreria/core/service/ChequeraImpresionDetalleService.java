/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.ChequeraImpresionDetalle;
import um.tesoreria.core.repository.IChequeraImpresionDetalleRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraImpresionDetalleService {

	@Autowired
	private IChequeraImpresionDetalleRepository repository;

	public List<ChequeraImpresionDetalle> saveAll(List<ChequeraImpresionDetalle> detalles) {
		return repository.saveAll(detalles);
	}

}
