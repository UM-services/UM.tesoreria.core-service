/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.NotificacionExamen;
import um.tesoreria.core.repository.view.NotificacionExamenRepository;

/**
 * @author daniel
 *
 */
@Service
public class NotificacionExamenService {

	@Autowired
	private NotificacionExamenRepository repository;

	public List<NotificacionExamen> findAllByFacultadId(Integer facultadId) {
		return repository.findAllByFacultadId(facultadId);
	}

}
