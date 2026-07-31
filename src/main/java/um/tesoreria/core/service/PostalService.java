/**
 * 
 */
package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.PostalException;
import um.tesoreria.core.model.Postal;
import um.tesoreria.core.repository.PostalRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class PostalService {
	private final PostalRepository repository;

	public Postal findByCodigopostal(Integer codigopostal) {
		return repository.findByCodigopostal(codigopostal).orElseThrow(() -> new PostalException(codigopostal));
	}
}
