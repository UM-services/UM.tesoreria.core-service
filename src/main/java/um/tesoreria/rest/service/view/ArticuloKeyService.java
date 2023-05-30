/**
 * 
 */
package um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.model.view.ArticuloKey;
import um.tesoreria.rest.repository.view.IArticuloKeyRepository;
import um.tesoreria.rest.repository.view.IArticuloKeyRepository;

/**
 * @author daniel
 *
 */
@Service
public class ArticuloKeyService {

	@Autowired
	private IArticuloKeyRepository repository;

	public List<ArticuloKey> findAllByStrings(List<String> conditions) {
		return repository.findAllByStrings(conditions);
	}

}
