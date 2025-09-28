/**
 * 
 */
package um.tesoreria.core.controller;

import java.math.BigDecimal;
import java.util.List;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.kotlin.model.Domicilio;
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

import um.tesoreria.core.exception.DomicilioException;
import um.tesoreria.core.model.view.DomicilioKey;
import um.tesoreria.core.service.DomicilioService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/domicilio")
@RequiredArgsConstructor
public class DomicilioController {

	private final DomicilioService service;

	@GetMapping("/{domicilioId}")
	public ResponseEntity<Domicilio> findByDomicilioId(@PathVariable Long domicilioId) {
		try {
			return new ResponseEntity<>(service.findByDomicilioId(domicilioId), HttpStatus.OK);
		} catch (DomicilioException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/unique/{personaId}/{documentoId}")
	public ResponseEntity<Domicilio> findByUnique(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId) {
		try {
            return ResponseEntity.ok(service.findByUnique(personaId, documentoId));
		} catch (DomicilioException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/pagador/{facultadId}/{personaId}/{documentoId}/{lectivoId}")
	public ResponseEntity<Domicilio> findWithPagador(@PathVariable Integer facultadId, @PathVariable BigDecimal personaId, @PathVariable Integer documentoId, @PathVariable Integer lectivoId) {
		try {
			return ResponseEntity.ok(service.findWithPagador(facultadId, personaId, documentoId, lectivoId));
		} catch (DomicilioException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}

	@PostMapping("/unifieds")
	public ResponseEntity<List<DomicilioKey>> findAllByUnifieds(@RequestBody List<String> unifieds) {
		return new ResponseEntity<>(service.findAllByUnifieds(unifieds), HttpStatus.OK);
	}

	@PostMapping("/")
	public ResponseEntity<Domicilio> add(@RequestBody Domicilio domicilio) {
		return new ResponseEntity<>(service.add(domicilio, true), HttpStatus.OK);
	}

	@PutMapping("/{domicilioId}")
	public ResponseEntity<Domicilio> update(@RequestBody Domicilio domicilio, @PathVariable Long domicilioId) {
		return new ResponseEntity<>(service.update(domicilio, domicilioId, true), HttpStatus.OK);
	}

	@PostMapping("/sincronize")
	public ResponseEntity<Domicilio> sincronize(@RequestBody Domicilio domicilio) {
		return new ResponseEntity<>(service.sincronize(domicilio), HttpStatus.OK);
	}

	@GetMapping("/capture/{personaId}/{documentoId}")
	public ResponseEntity<Integer> capture(@PathVariable BigDecimal personaId, @PathVariable Integer documentoId) {
		return new ResponseEntity<>(service.capture(personaId, documentoId), HttpStatus.OK);
	}
}
