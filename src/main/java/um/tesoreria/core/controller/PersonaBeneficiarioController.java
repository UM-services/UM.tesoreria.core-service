/**
 * 
 */
package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.core.exception.PersonaBeneficiarioException;
import um.tesoreria.core.model.PersonaBeneficiario;
import um.tesoreria.core.service.PersonaBeneficiarioService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/personaBeneficiario")
@RequiredArgsConstructor
public class PersonaBeneficiarioController {

	private final PersonaBeneficiarioService service;

	@GetMapping("/{personaUniqueId}")
	public ResponseEntity<PersonaBeneficiario> findByPersonaUniqueId(@PathVariable Long personaUniqueId) {
		try {
			return ResponseEntity.ok(service.findByPersonaUniqueId(personaUniqueId));
		} catch (PersonaBeneficiarioException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/")
	public ResponseEntity<PersonaBeneficiario> add(@RequestBody PersonaBeneficiario personaBeneficiario) {
		return ResponseEntity.ok(service.add(personaBeneficiario));
	}

	@PutMapping("/{personaBeneficiarioId}")
	public ResponseEntity<PersonaBeneficiario> update(@RequestBody PersonaBeneficiario personaBeneficiario,
			@PathVariable Long personaBeneficiarioId) {
		return ResponseEntity.ok(service.update(personaBeneficiario, personaBeneficiarioId));
	}

	@DeleteMapping("/{personaUniqueId}")
	public ResponseEntity<Void> delete(@PathVariable Long personaUniqueId) {
		service.delete(personaUniqueId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

}
