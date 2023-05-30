/**
 * 
 */
package um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.model.view.ContratadoPersona;
import um.tesoreria.rest.repository.view.IContratadoPersonaRepository;
import um.tesoreria.rest.repository.view.IContratadoPersonaRepository;

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
