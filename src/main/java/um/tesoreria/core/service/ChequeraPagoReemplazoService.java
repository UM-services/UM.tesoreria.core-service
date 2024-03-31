/**
 * 
 */
package um.tesoreria.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ChequeraPagoReemplazoException;
import um.tesoreria.core.model.ChequeraPagoReemplazo;
import um.tesoreria.core.repository.IChequeraPagoReemplazoRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraPagoReemplazoService {
	@Autowired
	private IChequeraPagoReemplazoRepository repository;

	public ChequeraPagoReemplazo findById(Long chequerapagoreemplazoID) {
		return repository.findById(chequerapagoreemplazoID).orElseThrow(() -> new ChequeraPagoReemplazoException(chequerapagoreemplazoID));
	}
}
