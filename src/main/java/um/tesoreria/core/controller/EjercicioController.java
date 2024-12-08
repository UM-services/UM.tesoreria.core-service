/**
 * 
 */
package um.tesoreria.core.controller;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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
import um.tesoreria.core.exception.EjercicioException;
import um.tesoreria.core.kotlin.model.Ejercicio;
import um.tesoreria.core.service.EjercicioService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/ejercicio")
public class EjercicioController {
	
	private final EjercicioService service;

	public EjercicioController(EjercicioService service) {
		this.service = service;
	}
	
	@GetMapping("/")
	public ResponseEntity<List<Ejercicio>> findAll() {
		return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
	}
	
	@GetMapping("/{ejercicioId}")
	public ResponseEntity<Ejercicio> findByEjercicioId(@PathVariable Integer ejercicioId) {
		return new ResponseEntity<>(service.findByEjercicioId(ejercicioId), HttpStatus.OK);
	}
	
	@GetMapping("/last")
	public ResponseEntity<Ejercicio> findLast() {
		return new ResponseEntity<>(service.findLast(), HttpStatus.OK);
	}
	
	@GetMapping("/fecha/{fecha}")
	public ResponseEntity<Ejercicio> findByFecha(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fecha) {
		try {
			return new ResponseEntity<>(service.findByFecha(fecha), HttpStatus.OK);
		} catch (EjercicioException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/")
	public ResponseEntity<Ejercicio> add(@RequestBody Ejercicio ejercicio) {
		return new ResponseEntity<>(service.add(ejercicio), HttpStatus.OK);
	}
	
	@PutMapping("/{ejercicioId}")
	public ResponseEntity<Ejercicio> update(@RequestBody Ejercicio ejercicio, @PathVariable Integer ejercicioId) {
		return new ResponseEntity<>(service.update(ejercicio, ejercicioId), HttpStatus.OK);
	}
	
	@DeleteMapping("/{ejercicioId}")
	public ResponseEntity<Void> delete(@PathVariable Integer ejercicioId) {
		service.delete(ejercicioId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
