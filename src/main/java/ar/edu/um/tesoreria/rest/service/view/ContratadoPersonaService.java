/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.view.ContratadoPersona;
import ar.edu.um.tesoreria.rest.repository.view.IContratadoPersonaRepository;

/**
 * @author daniel
 *
 */
@Service
public class ContratadoPersonaService {

	@Autowired
	private IContratadoPersonaRepository repository;

	public List<ContratadoPersona> findAll() {
		return repository.findAll();
	}

}
