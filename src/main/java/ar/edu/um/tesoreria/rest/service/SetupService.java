/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.SetupNotFoundException;
import ar.edu.um.tesoreria.rest.model.Setup;
import ar.edu.um.tesoreria.rest.repository.ISetupRepository;

/**
 * @author daniel
 *
 */
@Service
public class SetupService {

	@Autowired
	private ISetupRepository repository;

	public Setup findLast() {
		return repository.findTopByOrderBySetupIdDesc().orElseThrow(() -> new SetupNotFoundException());
	}

}
