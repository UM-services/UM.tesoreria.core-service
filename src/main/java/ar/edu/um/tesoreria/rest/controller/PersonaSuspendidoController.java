/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ar.edu.um.tesoreria.rest.exception.PersonaSuspendidoNotFoundException;
import ar.edu.um.tesoreria.rest.model.PersonaSuspendido;
import ar.edu.um.tesoreria.rest.service.PersonaSuspendidoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/personaSuspendido")
public class PersonaSuspendidoController {

	@Autowired
	private PersonaSuspendidoService service;

	@GetMapping("/sede/{facultadId}/{geograficaId}")
	public ResponseEntity<List<PersonaSuspendido>> findAllBySede(@PathVariable Integer facultadId,
			@PathVariable Integer geograficaId) {
		return new ResponseEntity<List<PersonaSuspendido>>(service.findAllBySede(facultadId, geograficaId),
				HttpStatus.OK);
	}

	@GetMapping("/{personaSuspendidoId}")
	public ResponseEntity<PersonaSuspendido> findByPersonaSuspendidoId(@PathVariable Long personaSuspendidoId) {
		try {
			return new ResponseEntity<PersonaSuspendido>(service.findByPersonaSuspendidoId(personaSuspendidoId),
					HttpStatus.OK);
		} catch (PersonaSuspendidoNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/unique/{personaId}/{documentoId}")
	public ResponseEntity<PersonaSuspendido> findByUnique(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId) {
		try {
			return new ResponseEntity<PersonaSuspendido>(service.findByUnique(personaId, documentoId), HttpStatus.OK);
		} catch (PersonaSuspendidoNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/")
	public ResponseEntity<PersonaSuspendido> add(@RequestBody PersonaSuspendido personaSuspendido) {
		return new ResponseEntity<PersonaSuspendido>(service.add(personaSuspendido), HttpStatus.OK);
	}

	@DeleteMapping("/{personaSuspendidoId}")
	public ResponseEntity<Void> delete(@PathVariable Long personaSuspendidoId) {
		service.delete(personaSuspendidoId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

}
