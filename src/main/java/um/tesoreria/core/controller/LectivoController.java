/**
 * 
 */
package um.tesoreria.core.controller;

import java.math.BigDecimal;
import java.util.List;

import um.tesoreria.core.kotlin.model.Lectivo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.service.LectivoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/lectivo", "/api/tesoreria/core/lectivo"})
public class LectivoController {

	private final LectivoService service;

	public LectivoController(LectivoService service) {
		this.service = service;
	}

	@GetMapping("/")
	public ResponseEntity<List<Lectivo>> findAll() {
		return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/reverse")
	public ResponseEntity<List<Lectivo>> findAllReverse() {
		return new ResponseEntity<>(service.findAllReverse(), HttpStatus.OK);
	}

	@GetMapping("/persona/{personaId}/{documentoId}")
	public ResponseEntity<List<Lectivo>> findAllByPersona(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId) {
		return new ResponseEntity<>(service.findAllByPersona(personaId, documentoId), HttpStatus.OK);
	}

	@GetMapping("/{lectivoId}")
	public ResponseEntity<Lectivo> findByLectivoId(@PathVariable Integer lectivoId) {
		return new ResponseEntity<>(service.findByLectivoId(lectivoId), HttpStatus.OK);
	}

	@GetMapping("/last")
	public ResponseEntity<Lectivo> findLast() {
		return new ResponseEntity<Lectivo>(service.findLast(), HttpStatus.OK);
	}

	@PostMapping("/")
	public ResponseEntity<Lectivo> add(@RequestBody Lectivo lectivo) {
		return new ResponseEntity<>(service.add(lectivo), HttpStatus.OK);
	}

	@DeleteMapping("/{lectivoId}")
	public ResponseEntity<Void> deleteByLectivoId(@PathVariable Integer lectivoId) {
		service.deleteByLectivoId(lectivoId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
