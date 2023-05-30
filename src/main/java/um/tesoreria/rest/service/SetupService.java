/**
 * 
 */
package um.tesoreria.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.exception.SetupException;
import um.tesoreria.rest.model.Setup;
import um.tesoreria.rest.repository.ISetupRepository;
import um.tesoreria.rest.exception.SetupException;
import um.tesoreria.rest.repository.ISetupRepository;

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
