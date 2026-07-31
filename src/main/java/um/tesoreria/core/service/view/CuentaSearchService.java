/**
 * 
 */
package um.tesoreria.core.service.view;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.CuentaSearch;
import um.tesoreria.core.repository.view.CuentaSearchRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class CuentaSearchService {

	private final CuentaSearchRepository repository;

	public List<CuentaSearch> findAllByStrings(List<String> conditions, Boolean visible) {
		return repository.findAllByStrings(conditions, visible);
	}

}
