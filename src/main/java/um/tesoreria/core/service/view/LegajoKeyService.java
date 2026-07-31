/**
 * 
 */
package um.tesoreria.core.service.view;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.LegajoKey;
import um.tesoreria.core.repository.view.LegajoKeyRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class LegajoKeyService {

	private final LegajoKeyRepository repository;

	public List<LegajoKey> findAllByUnifiedIn(List<String> keys) {
		return repository.findAllByUnifiedIn(keys);
	}

	public List<LegajoKey> findAllByFacultadIdAndUnifiedIn(Integer facultadId, List<String> keys) {
		return repository.findAllByFacultadIdAndUnifiedIn(facultadId, keys);
	}

}
