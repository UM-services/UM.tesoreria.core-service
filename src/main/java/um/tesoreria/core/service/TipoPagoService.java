/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.TipoPago;
import um.tesoreria.core.repository.TipoPagoRepository;

/**
 * @author daniel
 *
 */
@Service
public class TipoPagoService {
	
	@Autowired
	private TipoPagoRepository repository;

	public List<TipoPago> findAll() {
		return repository.findAll();
	}

}
