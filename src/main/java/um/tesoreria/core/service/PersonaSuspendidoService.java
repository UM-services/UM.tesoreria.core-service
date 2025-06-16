/**
 * 
 */
package um.tesoreria.core.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import um.tesoreria.core.exception.PersonaSuspendidoException;
import um.tesoreria.core.model.PersonaSuspendido;
import um.tesoreria.core.repository.PeriodoSuspendidoRepository;

/**
 * @author daniel
 *
 */
@Service
public class PersonaSuspendidoService {

	@Autowired
	private PeriodoSuspendidoRepository repository;

	public List<PersonaSuspendido> findAllBySede(Integer facultadId, Integer geograficaId) {
		return repository.findAllByFacultadIdAndGeograficaId(facultadId, geograficaId);
	}

	public PersonaSuspendido findByPersonaSuspendidoId(Long personaSuspendidoId) {
		return repository.findByPersonaSuspendidoId(personaSuspendidoId)
				.orElseThrow(() -> new PersonaSuspendidoException(personaSuspendidoId));
	}

	public PersonaSuspendido findByUnique(BigDecimal personaId, Integer documentoId) {
		return repository.findByPersonaIdAndDocumentoId(personaId, documentoId)
				.orElseThrow(() -> new PersonaSuspendidoException(personaId, documentoId));
	}

	public PersonaSuspendido add(PersonaSuspendido personaSuspendido) {
		personaSuspendido = repository.save(personaSuspendido);
		return personaSuspendido;
	}

	public PersonaSuspendido update(PersonaSuspendido newPersonaSuspendido, Long personaSuspendidoId) {
		return repository.findByPersonaSuspendidoId(personaSuspendidoId).map(personaSuspendido -> {
			personaSuspendido = new PersonaSuspendido(personaSuspendidoId, newPersonaSuspendido.getPersonaId(),
					newPersonaSuspendido.getDocumentoId(), newPersonaSuspendido.getFacultadId(),
					newPersonaSuspendido.getGeograficaId(), newPersonaSuspendido.getNotificado());
			personaSuspendido = repository.save(personaSuspendido);
			return personaSuspendido;
		}).orElseThrow(() -> new PersonaSuspendidoException(personaSuspendidoId));
	}

	@Transactional
	public void delete(Long personaSuspendidoId) {
		repository.deleteByPersonaSuspendidoId(personaSuspendidoId);
	}

}
