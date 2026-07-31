/**
 * 
 */
package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.Valor;
import um.tesoreria.core.repository.ValorRepository;
import um.tesoreria.core.exception.ValorException;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class ValorService {

	private final ValorRepository repository;

	public List<Valor> findAll() {
		return repository.findAll();
	}

	public Valor findByValorId(Integer valorId) {
		return repository.findByValorId(valorId).orElseThrow(() -> new ValorException(valorId));
	}

}
