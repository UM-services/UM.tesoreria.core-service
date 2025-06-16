/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.CargoMateria;
import um.tesoreria.core.repository.CargoMateriaRepository;

/**
 * @author daniel
 *
 */
@Service
public class CargoMateriaService {

	@Autowired
	private CargoMateriaRepository repository;

	public List<CargoMateria> findAll() {
		return repository.findAll();
	}

}
