/**
 * 
 */
package um.tesoreria.core.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.ArticuloKey;
import um.tesoreria.core.repository.view.ArticuloKeyRepository;

/**
 * @author daniel
 *
 */
@Service
public class ArticuloKeyService {

	@Autowired
	private ArticuloKeyRepository repository;

	public List<ArticuloKey> findAllByStrings(List<String> conditions) {
		return repository.findAllByStrings(conditions);
	}

}
