/**
 * 
 */
package um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.model.view.CarreraKey;
import um.tesoreria.rest.repository.view.ICarreraKeyRepository;
import um.tesoreria.rest.repository.view.ICarreraKeyRepository;

/**
 * @author daniel
 *
 */
@Service
public class CarreraKeyService {

	@Autowired
	private ICarreraKeyRepository repository;

	public List<CarreraKey> findAllByFacultadId(Integer facultadId) {
		return repository.findAllByFacultadId(facultadId);
	}

}
