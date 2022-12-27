/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ChequeraPagoReemplazoNotFoundException;
import ar.edu.um.tesoreria.rest.model.ChequeraPagoReemplazo;
import ar.edu.um.tesoreria.rest.repository.IChequeraPagoReemplazoRepository;

/**
 * @author daniel
 *
 */
@Service
public class ChequeraPagoReemplazoService {
	@Autowired
	private IChequeraPagoReemplazoRepository repository;

	public ChequeraPagoReemplazo findById(Long chequerapagoreemplazoID) {
		return repository.findById(chequerapagoreemplazoID).orElseThrow(() -> new ChequeraPagoReemplazoNotFoundException(chequerapagoreemplazoID));
	}
}
