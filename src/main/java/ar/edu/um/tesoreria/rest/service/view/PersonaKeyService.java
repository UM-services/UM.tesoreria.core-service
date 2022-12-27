/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.view.PersonaKey;
import ar.edu.um.tesoreria.rest.repository.view.IPersonaKeyRepository;

/**
 * @author daniel
 *
 */
@Service
public class PersonaKeyService {

	@Autowired
	private IPersonaKeyRepository repository;

	public List<PersonaKey> findAllByUnifiedIn(List<String> keys, Sort sort) {
		return repository.findAllByUnifiedIn(keys, sort);
	}

	public List<PersonaKey> findAllByStrings(List<String> conditions) {
		return repository.findAllByStrings(conditions);
	}

}
