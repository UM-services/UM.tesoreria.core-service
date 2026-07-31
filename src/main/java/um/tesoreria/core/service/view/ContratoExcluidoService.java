/**
 * 
 */
package um.tesoreria.core.service.view;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.ContratoExcluido;
import um.tesoreria.core.repository.view.ContratoExcluidoRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class ContratoExcluidoService {

	private final ContratoExcluidoRepository repository;

	public List<ContratoExcluido> findAll() {
		return repository.findAll();
	}

}
