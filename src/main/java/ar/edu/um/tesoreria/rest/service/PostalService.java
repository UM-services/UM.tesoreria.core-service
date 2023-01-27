/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.PostalException;
import ar.edu.um.tesoreria.rest.model.Postal;
import ar.edu.um.tesoreria.rest.repository.IPostalRepository;

/**
 * @author daniel
 *
 */
@Service
public class PostalService {
	@Autowired
	private IPostalRepository repository;

	public Postal findByCodigopostal(Integer codigopostal) {
		return repository.findByCodigopostal(codigopostal).orElseThrow(() -> new PostalException(codigopostal));
	}
}
