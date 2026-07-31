/**
 * 
 */
package um.tesoreria.core.service.view;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.GeograficaLectivo;
import um.tesoreria.core.repository.view.GeograficaLectivoRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class GeograficaLectivoService {

	private final GeograficaLectivoRepository repository;

	public List<GeograficaLectivo> findAllByLectivoId(Integer lectivoId) {
		return repository.findAllByLectivoId(lectivoId);
	}

}
