/**
 * 
 */
package um.tesoreria.core.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.GeograficaLectivo;
import um.tesoreria.core.repository.view.GeograficaLectivoRepository;

/**
 * @author daniel
 *
 */
@Service
public class GeograficaLectivoService {

	@Autowired
	private GeograficaLectivoRepository repository;

	public List<GeograficaLectivo> findAllByLectivoId(Integer lectivoId) {
		return repository.findAllByLectivoId(lectivoId);
	}

}
