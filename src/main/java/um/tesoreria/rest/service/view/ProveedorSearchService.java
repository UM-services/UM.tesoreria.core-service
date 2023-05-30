/**
 * 
 */
package um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.model.view.ProveedorSearch;
import um.tesoreria.rest.repository.view.IProveedorSearchRepository;
import um.tesoreria.rest.repository.view.IProveedorSearchRepository;

/**
 * @author daniel
 *
 */
@Service
public class ProveedorSearchService {

	@Autowired
	private IProveedorSearchRepository repository;

	public List<ProveedorSearch> findAllByStrings(List<String> conditions) {
		return repository.findAllByStrings(conditions);
	}

}
