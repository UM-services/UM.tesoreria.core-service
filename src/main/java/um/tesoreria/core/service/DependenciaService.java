/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.Dependencia;
import um.tesoreria.core.repository.IDependenciaRepository;

/**
 * @author daniel
 *
 */
@Service
public class DependenciaService {
	
	@Autowired
	private IDependenciaRepository repository;

	public List<Dependencia> findAll() {
		return repository.findAll();
	}

}
