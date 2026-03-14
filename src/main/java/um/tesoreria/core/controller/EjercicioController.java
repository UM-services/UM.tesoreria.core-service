/**
 * 
 */
package um.tesoreria.core.controller;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class EjercicioController {
	
	private final EjercicioService service;

	@GetMapping("/")
	public ResponseEntity<List<Ejercicio>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}
	
	@GetMapping("/{ejercicioId}")
	public ResponseEntity<Ejercicio> findByEjercicioId(@PathVariable Integer ejercicioId) {
		try {
			return ResponseEntity.ok(service.findByEjercicioId(ejercicioId));
		} catch (EjercicioException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	
	@GetMapping("/last")
	public ResponseEntity<Ejercicio> findLast() {
		return ResponseEntity.ok(service.findLast());
	}
	
	@GetMapping("/fecha/{fecha}")
	public ResponseEntity<Ejercicio> findByFecha(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fecha) {
		try {
			return new ResponseEntity<>(service.findByFecha(fecha), HttpStatus.OK);
		} catch (EjercicioException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	
	@PostMapping("/")
	public ResponseEntity<Ejercicio> add(@RequestBody Ejercicio ejercicio) {
		return ResponseEntity.ok(service.add(ejercicio));
	}
	
	@PutMapping("/{ejercicioId}")
	public ResponseEntity<Ejercicio> update(@RequestBody Ejercicio ejercicio, @PathVariable Integer ejercicioId) {
		return ResponseEntity.ok(service.update(ejercicio, ejercicioId));
	}
	
	@DeleteMapping("/{ejercicioId}")
	public ResponseEntity<Void> delete(@PathVariable Integer ejercicioId) {
		service.delete(ejercicioId);
		return ResponseEntity.noContent().build();
	}

}
