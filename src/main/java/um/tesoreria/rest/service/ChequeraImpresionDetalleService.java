/**
 * 
 */
package um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.kotlin.model.ChequeraImpresionDetalle;
import um.tesoreria.rest.repository.IChequeraImpresionDetalleRepository;
import um.tesoreria.rest.repository.IChequeraImpresionDetalleRepository;

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
