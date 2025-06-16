/**
 * 
 */
package um.tesoreria.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.SetupException;
import um.tesoreria.core.kotlin.model.Setup;
import um.tesoreria.core.repository.SetupRepository;

/**
 * @author daniel
 *
 */
@Service
public class SetupService {

	private final SetupRepository repository;

	@Autowired
	public SetupService(SetupRepository repository) {
		this.repository = repository;
	}

	public Setup findLast() {
		return repository.findTopByOrderBySetupIdDesc().orElseThrow(() -> new SetupException());
	}

}
