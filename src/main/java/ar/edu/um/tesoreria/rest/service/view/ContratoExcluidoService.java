/**
 * 
 */
package ar.edu.um.tesoreria.rest.service.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.view.ContratoExcluido;
import ar.edu.um.tesoreria.rest.repository.view.IContratoExcluidoRepository;

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
