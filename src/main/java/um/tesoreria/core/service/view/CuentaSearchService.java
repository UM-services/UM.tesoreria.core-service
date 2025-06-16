/**
 * 
 */
package um.tesoreria.core.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.CuentaSearch;
import um.tesoreria.core.repository.view.CuentaSearchRepository;

/**
 * @author daniel
 *
 */
@Service
public class CuentaSearchService {

	@Autowired
	private CuentaSearchRepository repository;

	public List<CuentaSearch> findAllByStrings(List<String> conditions, Boolean visible) {
		return repository.findAllByStrings(conditions, visible);
	}

}
