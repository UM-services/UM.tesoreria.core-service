/**
 * 
 */
package um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.model.UbicacionArticulo;
import um.tesoreria.rest.repository.IUbicacionArticuloRepository;
import um.tesoreria.rest.model.UbicacionArticulo;
import um.tesoreria.rest.repository.IUbicacionArticuloRepository;

/**
 * @author daniel
 *
 */
@Service
public class UbicacionArticuloService {

	@Autowired
	private IUbicacionArticuloRepository repository;

	public List<UbicacionArticulo> findAll() {
		return repository.findAll();
	}

}
