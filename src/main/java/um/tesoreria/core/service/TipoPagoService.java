/**
 * 
 */
package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.TipoPago;
import um.tesoreria.core.repository.TipoPagoRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class TipoPagoService {
	
	private final TipoPagoRepository repository;

	public List<TipoPago> findAll() {
		return repository.findAll();
	}

}
