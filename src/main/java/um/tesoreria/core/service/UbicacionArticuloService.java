/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.UbicacionArticulo;
import um.tesoreria.core.repository.UbicacionArticuloRepository;

/**
 * @author daniel
 *
 */
@Service
public class UbicacionArticuloService {

	@Autowired
	private UbicacionArticuloRepository repository;

	public List<UbicacionArticulo> findAll() {
		return repository.findAll();
	}

}
