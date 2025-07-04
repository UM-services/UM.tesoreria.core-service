/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

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

import um.tesoreria.core.exception.TipoChequeraException;
import um.tesoreria.core.model.view.TipoChequeraSearch;
import um.tesoreria.core.service.TipoChequeraService;
import um.tesoreria.core.kotlin.model.TipoChequera;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/tipoChequera", "/api/tesoreria/core/tipoChequera"})
public class TipoChequeraController {

	private final TipoChequeraService service;

	public TipoChequeraController(TipoChequeraService service) {
		this.service = service;
	}

	@GetMapping("/")
	public ResponseEntity<List<TipoChequera>> findAll() {
		return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
	}

	@PostMapping("/search")
	public ResponseEntity<List<TipoChequeraSearch>> findAllByStrings(@RequestBody List<String> conditions) {
		return ResponseEntity.ok(service.findAllByStrings(conditions));
	}

	@GetMapping("/asignable/{facultadId}/{lectivoId}/{geograficaId}/{claseChequeraId}")
	public ResponseEntity<List<TipoChequera>> findAllAsignable(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer geograficaId,
			@PathVariable Integer claseChequeraId) {
		return new ResponseEntity<>(
				service.findAllAsignable(facultadId, lectivoId, geograficaId, claseChequeraId), HttpStatus.OK);
	}

	@GetMapping("/facultad/{facultadId}/geografica/{geograficaId}")
	public ResponseEntity<List<TipoChequera>> findAllByFacultadIdAndGeograficaId(@PathVariable Integer facultadId,
			@PathVariable Integer geograficaId) {
		return new ResponseEntity<>(
				service.findAllByFacultadIdAndGeograficaId(facultadId, geograficaId), HttpStatus.OK);
	}

	@GetMapping("/{tipoChequeraId}")
	public ResponseEntity<TipoChequera> findByTipoChequeraId(@PathVariable Integer tipoChequeraId) {
		try {
			return new ResponseEntity<>(service.findByTipoChequeraId(tipoChequeraId), HttpStatus.OK);
		} catch (TipoChequeraException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/last")
	public ResponseEntity<TipoChequera> findLast() {
		try {
			return new ResponseEntity<>(service.findLast(), HttpStatus.OK);
		} catch (TipoChequeraException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/")
	public ResponseEntity<TipoChequera> add(@RequestBody TipoChequera tipochequera) {
		return new ResponseEntity<>(service.add(tipochequera), HttpStatus.OK);
	}

	@PutMapping("/{tipochequeraId}")
	public ResponseEntity<TipoChequera> update(@RequestBody TipoChequera tipochequera,
			@PathVariable Integer tipochequeraId) {
		return new ResponseEntity<>(service.update(tipochequera, tipochequeraId), HttpStatus.OK);
	}

	@DeleteMapping("/{tipochequeraId}")
	public ResponseEntity<Void> delete(@PathVariable Integer tipochequeraId) {
		service.delete(tipochequeraId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/unmark")
	public ResponseEntity<Void> unmark() {
		service.unmark();
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/mark/{tipochequeraId}/{imprimir}")
	public ResponseEntity<Void> mark(@PathVariable Integer tipochequeraId, @PathVariable Byte imprimir) {
		service.mark(tipochequeraId, imprimir);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
