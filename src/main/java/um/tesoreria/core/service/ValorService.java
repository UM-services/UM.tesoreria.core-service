/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.Valor;
import um.tesoreria.core.repository.ValorRepository;
import um.tesoreria.core.exception.ValorException;

/**
 * @author daniel
 *
 */
@Service
public class ValorService {

	@Autowired
	private ValorRepository repository;

	public List<Valor> findAll() {
		return repository.findAll();
	}

	public Valor findByValorId(Integer valorId) {
		return repository.findByValorId(valorId).orElseThrow(() -> new ValorException(valorId));
	}

}
