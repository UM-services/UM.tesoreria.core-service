/**
 * 
 */
package um.tesoreria.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.PostalException;
import um.tesoreria.core.model.Postal;
import um.tesoreria.core.repository.PostalRepository;

/**
 * @author daniel
 *
 */
@Service
public class PostalService {
	@Autowired
	private PostalRepository repository;

	public Postal findByCodigopostal(Integer codigopostal) {
		return repository.findByCodigopostal(codigopostal).orElseThrow(() -> new PostalException(codigopostal));
	}
}
