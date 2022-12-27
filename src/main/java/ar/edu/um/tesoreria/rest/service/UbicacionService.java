/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.Ubicacion;
import ar.edu.um.tesoreria.rest.repository.IUbicacionRepository;

/**
 * @author daniel
 *
 */
@Service
public class UbicacionService {
	
	@Autowired
	private IUbicacionRepository repository;

	public List<Ubicacion> findAll() {
		return repository.findAll();
	}

}
