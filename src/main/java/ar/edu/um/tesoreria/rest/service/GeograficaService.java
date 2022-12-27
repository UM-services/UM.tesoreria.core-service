/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.GeograficaNotFoundException;
import ar.edu.um.tesoreria.rest.model.Geografica;
import ar.edu.um.tesoreria.rest.model.view.GeograficaLectivo;
import ar.edu.um.tesoreria.rest.repository.IGeograficaRepository;
import ar.edu.um.tesoreria.rest.repository.view.IGeograficaLectivoRepository;

/**
 * @author daniel
 *
 */
@Service
public class GeograficaService {
	@Autowired
	private IGeograficaRepository repository;
	
	@Autowired
	private IGeograficaLectivoRepository geograficalectivorepository;
	
	public List<Geografica> findAll() {
		return repository.findAll();
	}

	public List<GeograficaLectivo> findAllByLectivoId(Integer lectivoId) {
		return geograficalectivorepository.findAllByLectivoId(lectivoId);
	}

	public Geografica findByGeograficaId(Integer geograficaId) {
		return repository.findByGeograficaId(geograficaId).orElseThrow(() -> new GeograficaNotFoundException(geograficaId));
	}

}
