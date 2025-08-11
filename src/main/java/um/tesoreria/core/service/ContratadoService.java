/**
 * 
 */
package um.tesoreria.core.service;

import lombok.extern.slf4j.Slf4j;
import um.tesoreria.core.kotlin.model.Contratado;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ContratadoException;
import um.tesoreria.core.repository.ContratadoRepository;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class ContratadoService {

	private final ContratadoRepository repository;

    public ContratadoService(ContratadoRepository repository) {
        this.repository = repository;
    }

	public Contratado findByContratadoId(Long contratadoId) {
		return repository.findByContratadoId(contratadoId)
				.orElseThrow(() -> new ContratadoException(contratadoId));
	}

	public Contratado findByPersona(Long personaClave) {
        log.debug("Processing ContratadoService.findByPersona()");
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
