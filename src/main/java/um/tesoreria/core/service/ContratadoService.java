/**
 * 
 */
package um.tesoreria.core.service;

import um.tesoreria.core.kotlin.model.Contratado;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ContratadoException;
import um.tesoreria.core.repository.ContratadoRepository;

/**
 * @author daniel
 *
 */
@Service
public class ContratadoService {

	@Autowired
	private ContratadoRepository repository;

	public Contratado findByContratadoId(Long contratadoId) {
		return repository.findByContratadoId(contratadoId)
				.orElseThrow(() -> new ContratadoException(contratadoId));
	}

	public Contratado findByPersona(Long personaClave) {
		return repository.findByPersonaClave(personaClave)
				.orElseThrow(() -> new ContratadoException(personaClave, true));
	}

	public Contratado add(Contratado contratado) {
		contratado = repository.save(contratado);
		return contratado;
	}

	@Transactional
	public void delete(Long contratadoId) {
		repository.deleteByContratadoId(contratadoId);
	}

}
