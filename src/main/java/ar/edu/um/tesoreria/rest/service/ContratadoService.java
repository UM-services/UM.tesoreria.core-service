/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ContratadoNotFoundException;
import ar.edu.um.tesoreria.rest.model.Contratado;
import ar.edu.um.tesoreria.rest.repository.IContratadoRepository;

/**
 * @author daniel
 *
 */
@Service
public class ContratadoService {

	@Autowired
	private IContratadoRepository repository;

	public Contratado findByContratadoId(Long contratadoId) {
		return repository.findByContratadoId(contratadoId)
				.orElseThrow(() -> new ContratadoNotFoundException(contratadoId));
	}

	public Contratado findByPersona(Long personaClave) {
		return repository.findByPersonaClave(personaClave)
				.orElseThrow(() -> new ContratadoNotFoundException(personaClave, true));
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
