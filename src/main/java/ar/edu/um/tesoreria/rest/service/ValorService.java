/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.Valor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.repository.IValorRepository;
import ar.edu.um.tesoreria.rest.exception.ValorException;

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
