/**
 * 
 */
package um.tesoreria.core.service;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.PersonaBeneficiarioException;
import um.tesoreria.core.model.PersonaBeneficiario;
import um.tesoreria.core.repository.PersonaBeneficiarioRepository;

/**
 * @author daniel
 *
 */
@Service
public class PersonaBeneficiarioService {

	@Autowired
	private PersonaBeneficiarioRepository repository;

	public PersonaBeneficiario findByPersonaUniqueId(Long personaUniqueId) {
		return repository.findByPersonaUniqueId(personaUniqueId)
				.orElseThrow(() -> new PersonaBeneficiarioException(personaUniqueId, true));
	}

	public PersonaBeneficiario add(PersonaBeneficiario personaBeneficiario) {
		personaBeneficiario = repository.save(personaBeneficiario);
		return personaBeneficiario;
	}

	public PersonaBeneficiario update(PersonaBeneficiario newPersonaBeneficiario, Long personaBeneficiarioId) {
		return repository.findByPersonaBeneficiarioId(personaBeneficiarioId).map(personaBeneficiario -> {
			personaBeneficiario = new PersonaBeneficiario(personaBeneficiarioId,
					newPersonaBeneficiario.getPersonaUniqueId(), newPersonaBeneficiario.getDocumento(),
					newPersonaBeneficiario.getApellidoNombre(), newPersonaBeneficiario.getCuit(),
					newPersonaBeneficiario.getCbu(), null);
			personaBeneficiario = repository.save(personaBeneficiario);
			return personaBeneficiario;
		}).orElseThrow(() -> new PersonaBeneficiarioException(personaBeneficiarioId));
	}

	@Transactional
	public void delete(Long personaUniqueId) {
		repository.deleteByPersonaUniqueId(personaUniqueId);
	}

}
