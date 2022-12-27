/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.view.CuentaSearch;
import ar.edu.um.tesoreria.rest.repository.view.ICuentaSearchRepository;

/**
 * @author daniel
 *
 */
@Service
public class CuentaSearchService {

	@Autowired
	private ICuentaSearchRepository repository;

	public List<CuentaSearch> findAllByStrings(List<String> conditions, Boolean visible) {
		return repository.findAllByStrings(conditions, visible);
	}

}
