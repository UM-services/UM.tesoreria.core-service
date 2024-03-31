/**
 * 
 */
package um.tesoreria.core.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.ContratoExcluido;
import um.tesoreria.core.repository.view.IContratoExcluidoRepository;

/**
 * @author daniel
 *
 */
@Service
public class ContratoExcluidoService {

	@Autowired
	private IContratoExcluidoRepository repository;

	public List<ContratoExcluido> findAll() {
		return repository.findAll();
	}

}
