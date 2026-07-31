/**
 * 
 */
package um.tesoreria.core.service.view;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.CarreraKey;
import um.tesoreria.core.repository.view.CarreraKeyRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class CarreraKeyService {

	private final CarreraKeyRepository repository;

	public List<CarreraKey> findAllByFacultadId(Integer facultadId) {
		return repository.findAllByFacultadId(facultadId);
	}

}
