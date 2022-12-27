/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.TipoPago;
import ar.edu.um.tesoreria.rest.repository.ITipoPagoRepository;

/**
 * @author daniel
 *
 */
@Service
public class TipoPagoService {
	
	@Autowired
	private ITipoPagoRepository repository;

	public List<TipoPago> findAll() {
		return repository.findAll();
	}

}
