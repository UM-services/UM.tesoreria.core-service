/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.view.GeograficaLectivo;
import ar.edu.um.tesoreria.rest.repository.view.IGeograficaLectivoRepository;

/**
 * @author daniel
 *
 */
@Service
public class GeograficaLectivoService {

	@Autowired
	private IGeograficaLectivoRepository repository;

	public List<GeograficaLectivo> findAllByLectivoId(Integer lectivoId) {
		return repository.findAllByLectivoId(lectivoId);
	}

}
