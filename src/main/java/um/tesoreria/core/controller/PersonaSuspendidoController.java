/**
 * 
 */
package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

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

import um.tesoreria.core.exception.PersonaSuspendidoException;
import um.tesoreria.core.model.PersonaSuspendido;
import um.tesoreria.core.service.PersonaSuspendidoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/personaSuspendido")
@RequiredArgsConstructor
public class PersonaSuspendidoController {

	private final PersonaSuspendidoService service;

	@GetMapping("/sede/{facultadId}/{geograficaId}")
	public ResponseEntity<List<PersonaSuspendido>> findAllBySede(@PathVariable Integer facultadId,
			@PathVariable Integer geograficaId) {
		return ResponseEntity.ok(service.findAllBySede(facultadId, geograficaId));
	}

	@GetMapping("/{personaSuspendidoId}")
	public ResponseEntity<PersonaSuspendido> findByPersonaSuspendidoId(@PathVariable Long personaSuspendidoId) {
		try {
			return ResponseEntity.ok(service.findByPersonaSuspendidoId(personaSuspendidoId));
		} catch (PersonaSuspendidoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/unique/{personaId}/{documentoId}")
	public ResponseEntity<PersonaSuspendido> findByUnique(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId) {
		try {
			return ResponseEntity.ok(service.findByUnique(personaId, documentoId));
		} catch (PersonaSuspendidoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/")
	public ResponseEntity<PersonaSuspendido> add(@RequestBody PersonaSuspendido personaSuspendido) {
		return ResponseEntity.ok(service.add(personaSuspendido));
	}

	@DeleteMapping("/{personaSuspendidoId}")
	public ResponseEntity<Void> delete(@PathVariable Long personaSuspendidoId) {
		service.delete(personaSuspendidoId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

}
