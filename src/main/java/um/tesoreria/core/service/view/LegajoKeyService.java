/**
 * 
 */
package um.tesoreria.core.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.LegajoKey;
import um.tesoreria.core.repository.view.ILegajoKeyRepository;

/**
 * @author daniel
 *
 */
@Service
public class LegajoKeyService {

	@Autowired
	private ILegajoKeyRepository repository;

	public List<LegajoKey> findAllByUnifiedIn(List<String> keys) {
		return repository.findAllByUnifiedIn(keys);
	}

	public List<LegajoKey> findAllByFacultadIdAndUnifiedIn(Integer facultadId, List<String> keys) {
		return repository.findAllByFacultadIdAndUnifiedIn(facultadId, keys);
	}

}
