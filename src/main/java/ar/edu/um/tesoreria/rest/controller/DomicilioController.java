/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.math.BigDecimal;
import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.Domicilio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ar.edu.um.tesoreria.rest.exception.DomicilioException;
import ar.edu.um.tesoreria.rest.model.view.DomicilioKey;
import ar.edu.um.tesoreria.rest.service.DomicilioService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/domicilio")
public class DomicilioController {

	@Autowired
	private DomicilioService service;

	@GetMapping("/{domicilioId}")
	public ResponseEntity<Domicilio> findByDomicilioId(@PathVariable Long domicilioId) {
		try {
			return new ResponseEntity<Domicilio>(service.findByDomicilioId(domicilioId), HttpStatus.OK);
		} catch (DomicilioException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/unique/{personaId}/{documentoId}")
	public ResponseEntity<Domicilio> findByUnique(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId) {
		try {
			return new ResponseEntity<Domicilio>(service.findByUnique(personaId, documentoId), HttpStatus.OK);
		} catch (DomicilioException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/unifieds")
	public ResponseEntity<List<DomicilioKey>> findAllByUnifieds(@RequestBody List<String> unifieds) {
		return new ResponseEntity<List<DomicilioKey>>(service.findAllByUnifieds(unifieds), HttpStatus.OK);
	}

	@PostMapping("/")
	public ResponseEntity<Domicilio> add(@RequestBody Domicilio domicilio) {
		return new ResponseEntity<Domicilio>(service.add(domicilio, true), HttpStatus.OK);
	}

	@PutMapping("/{domicilioId}")
	public ResponseEntity<Domicilio> update(@RequestBody Domicilio domicilio, @PathVariable Long domicilioId) {
		return new ResponseEntity<Domicilio>(service.update(domicilio, domicilioId, true), HttpStatus.OK);
	}

	@PostMapping("/sincronize")
	public ResponseEntity<Domicilio> sincronize(@RequestBody Domicilio domicilio) {
		return new ResponseEntity<Domicilio>(service.sincronize(domicilio), HttpStatus.OK);
	}

	@GetMapping("/capture/{personaId}/{documentoId}")
	public ResponseEntity<Integer> capture(@PathVariable BigDecimal personaId, @PathVariable Integer documentoId) {
		return new ResponseEntity<Integer>(service.capture(personaId, documentoId), HttpStatus.OK);
	}
}
