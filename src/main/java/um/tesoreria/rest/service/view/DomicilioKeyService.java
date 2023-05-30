/**
 * 
 */
package um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.model.view.DomicilioKey;
import um.tesoreria.rest.repository.view.IDomicilioKeyRepository;
import um.tesoreria.rest.repository.view.IDomicilioKeyRepository;

/**
 * @author daniel
 *
 */
@Service
public class DomicilioKeyService {

	@Autowired
	private IDomicilioKeyRepository repository;

	public List<DomicilioKey> findAllByUnifiedIn(List<String> keys) {
		return repository.findAllByUnifiedIn(keys);
	}

}
