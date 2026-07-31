/**
 * 
 */
package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.CargoMateria;
import um.tesoreria.core.repository.CargoMateriaRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class CargoMateriaService {

	private final CargoMateriaRepository repository;

	public List<CargoMateria> findAll() {
		return repository.findAll();
	}

}
