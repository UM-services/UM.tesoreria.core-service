/**
 * 
 */
package um.tesoreria.rest.controller;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import um.tesoreria.rest.kotlin.model.Ejercicio;
import um.tesoreria.rest.service.EjercicioService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/ejercicio")
public class EjercicioController {
	
	@Autowired
	private EjercicioService service;
	
	@GetMapping("/")
	public ResponseEntity<List<Ejercicio>> findAll() {
		return new ResponseEntity<List<Ejercicio>>(service.findAll(), HttpStatus.OK);
	}
	
	@GetMapping("/{ejercicioId}")
	public ResponseEntity<Ejercicio> findByEjercicioId(@PathVariable Integer ejercicioId) {
		return new ResponseEntity<Ejercicio>(service.findByEjercicioId(ejercicioId), HttpStatus.OK);
	}
	
	@GetMapping("/last")
	public ResponseEntity<Ejercicio> findLast() {
		return new ResponseEntity<Ejercicio>(service.findLast(), HttpStatus.OK);
	}
	
	@GetMapping("/fecha/{fecha}")
	public ResponseEntity<Ejercicio> findByFecha(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fecha) {
		return new ResponseEntity<Ejercicio>(service.findByFecha(fecha), HttpStatus.OK);
	}
	
	@PostMapping("/")
	public ResponseEntity<Ejercicio> add(@RequestBody Ejercicio ejercicio) {
		return new ResponseEntity<Ejercicio>(service.add(ejercicio), HttpStatus.OK);
	}
	
	@PutMapping("/{ejercicioId}")
	public ResponseEntity<Ejercicio> update(@RequestBody Ejercicio ejercicio, @PathVariable Integer ejercicioId) {
		return new ResponseEntity<Ejercicio>(service.update(ejercicio, ejercicioId), HttpStatus.OK);
	}
	
	@DeleteMapping("/{ejercicioId}")
	public ResponseEntity<Void> delete(@PathVariable Integer ejercicioId) {
		service.delete(ejercicioId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
}
