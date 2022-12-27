/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.NotificacionExamen;
import ar.edu.um.tesoreria.rest.repository.view.INotificacionExamenRepository;

/**
 * @author daniel
 *
 */
@Service
public class NotificacionExamenService {

	@Autowired
	private INotificacionExamenRepository repository;

	public List<NotificacionExamen> findAllByFacultadId(Integer facultadId) {
		return repository.findAllByFacultadId(facultadId);
	}

}
