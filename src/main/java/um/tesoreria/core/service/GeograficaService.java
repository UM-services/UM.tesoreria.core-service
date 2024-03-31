/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.GeograficaException;
import um.tesoreria.core.kotlin.model.Geografica;
import um.tesoreria.core.model.view.GeograficaLectivo;
import um.tesoreria.core.repository.IGeograficaRepository;
import um.tesoreria.core.service.view.GeograficaLectivoService;

/**
 * @author daniel
 *
 */
@Service
public class GeograficaService {

	@Autowired
	private IGeograficaRepository repository;

	@Autowired
	private GeograficaLectivoService geograficaLectivoService;

	public List<Geografica> findAll() {
		return repository.findAll();
	}

	public List<Geografica> findAllBySede(Integer geograficaId) {
		if (geograficaId == 0) {
			return repository.findAll();
		}
		return repository.findAllByGeograficaId(geograficaId);
	}

	public List<GeograficaLectivo> findAllByLectivoId(Integer lectivoId) {
		return geograficaLectivoService.findAllByLectivoId(lectivoId);
	}

	public Geografica findByGeograficaId(Integer geograficaId) {
		return repository.findByGeograficaId(geograficaId).orElseThrow(() -> new GeograficaException(geograficaId));
	}

}
