/**
 * 
 */
package um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.kotlin.model.CargoMateria;
import um.tesoreria.rest.repository.ICargoMateriaRepository;

/**
 * @author daniel
 *
 */
@Service
public class CargoMateriaService {

	@Autowired
	private ICargoMateriaRepository repository;

	public List<CargoMateria> findAll() {
		return repository.findAll();
	}

}
