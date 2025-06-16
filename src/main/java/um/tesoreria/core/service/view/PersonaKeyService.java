/**
 * 
 */
package um.tesoreria.core.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.PersonaKey;
import um.tesoreria.core.repository.view.PersonaKeyRepository;

/**
 * @author daniel
 *
 */
@Service
public class PersonaKeyService {

	@Autowired
	private PersonaKeyRepository repository;

	public List<PersonaKey> findAllByUnifiedIn(List<String> keys, Sort sort) {
		return repository.findAllByUnifiedIn(keys, sort);
	}

	public List<PersonaKey> findAllByStrings(List<String> conditions) {
		return repository.findAllByStrings(conditions);
	}

}
