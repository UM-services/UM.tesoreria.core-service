/**
 * 
 */
package um.tesoreria.core.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.ContratadoPersona;
import um.tesoreria.core.repository.view.IContratadoPersonaRepository;

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
