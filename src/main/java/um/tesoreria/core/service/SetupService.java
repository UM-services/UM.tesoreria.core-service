/**
 * 
 */
package um.tesoreria.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.SetupException;
import um.tesoreria.core.model.Setup;
import um.tesoreria.core.repository.ISetupRepository;

/**
 * @author daniel
 *
 */
@Service
public class SetupService {

	@Autowired
	private ISetupRepository repository;

	public Setup findLast() {
		return repository.findTopByOrderBySetupIdDesc().orElseThrow(() -> new SetupException());
	}

}
