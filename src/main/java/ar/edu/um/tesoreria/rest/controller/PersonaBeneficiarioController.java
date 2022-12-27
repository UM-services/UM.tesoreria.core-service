/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

import ar.edu.um.tesoreria.rest.exception.PersonaBeneficiarioNotFoundException;
import ar.edu.um.tesoreria.rest.model.PersonaBeneficiario;
import ar.edu.um.tesoreria.rest.service.PersonaBeneficiarioService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/personaBeneficiario")
public class PersonaBeneficiarioController {

	@Autowired
	private PersonaBeneficiarioService service;

	@GetMapping("/{personaUniqueId}")
	public ResponseEntity<PersonaBeneficiario> findByPersonaUniqueId(@PathVariable Long personaUniqueId) {
		try {
			return new ResponseEntity<PersonaBeneficiario>(service.findByPersonaUniqueId(personaUniqueId), HttpStatus.OK);
		} catch (PersonaBeneficiarioNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/")
	public ResponseEntity<PersonaBeneficiario> add(@RequestBody PersonaBeneficiario personaBeneficiario) {
		return new ResponseEntity<PersonaBeneficiario>(service.add(personaBeneficiario), HttpStatus.OK);
	}

	@PutMapping("/{personaBeneficiarioId}")
	public ResponseEntity<PersonaBeneficiario> update(@RequestBody PersonaBeneficiario personaBeneficiario,
			@PathVariable Long personaBeneficiarioId) {
		return new ResponseEntity<PersonaBeneficiario>(service.update(personaBeneficiario, personaBeneficiarioId),
				HttpStatus.OK);
	}

	@DeleteMapping("/{personaUniqueId}")
	public ResponseEntity<Void> delete(@PathVariable Long personaUniqueId) {
		service.delete(personaUniqueId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

}
