/**
 * 
 */
package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.model.NotificacionExamen;
import um.tesoreria.core.repository.view.NotificacionExamenRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class NotificacionExamenService {

	private final NotificacionExamenRepository repository;

	public List<NotificacionExamen> findAllByFacultadId(Integer facultadId) {
		return repository.findAllByFacultadId(facultadId);
	}

}
