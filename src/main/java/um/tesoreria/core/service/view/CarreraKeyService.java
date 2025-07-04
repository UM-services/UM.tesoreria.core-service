/**
 * 
 */
package um.tesoreria.core.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.CarreraKey;
import um.tesoreria.core.repository.view.CarreraKeyRepository;

/**
 * @author daniel
 *
 */
@Service
public class CarreraKeyService {

	@Autowired
	private CarreraKeyRepository repository;

	public List<CarreraKey> findAllByFacultadId(Integer facultadId) {
		return repository.findAllByFacultadId(facultadId);
	}

}
