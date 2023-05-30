/**
 * 
 */
package um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.kotlin.model.Valor;
import um.tesoreria.rest.repository.IValorRepository;
import um.tesoreria.rest.exception.ValorException;
import um.tesoreria.rest.exception.ValorException;
import um.tesoreria.rest.repository.IValorRepository;

/**
 * @author daniel
 *
 */
@Service
public class ValorService {

	@Autowired
	private IValorRepository repository;

	public List<Valor> findAll() {
		return repository.findAll();
	}

	public Valor findByValorId(Integer valorId) {
		return repository.findByValorId(valorId).orElseThrow(() -> new ValorException(valorId));
	}

}
