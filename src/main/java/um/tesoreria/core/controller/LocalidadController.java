/**
 * 
 */
package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.core.exception.LocalidadException;
import um.tesoreria.core.model.Localidad;
import um.tesoreria.core.service.LocalidadService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/localidad")
@RequiredArgsConstructor
public class LocalidadController {

	private final LocalidadService service;

	@GetMapping("/provincia/{facultadId}/{provinciaId}")
	public ResponseEntity<List<Localidad>> findAllByProvinciaId(@PathVariable Integer facultadId,
			@PathVariable Integer provinciaId) {
		return ResponseEntity.ok(service.findAllByProvinciaId(facultadId, provinciaId));
	}

	@PostMapping("/nombre")
	public ResponseEntity<Localidad> findByNombre(@RequestBody Localidad localidad) {
		try {
			return ResponseEntity.ok(
					service.findByNombre(localidad.getFacultadId(), localidad.getProvinciaId(), localidad.getNombre()));
		} catch (LocalidadException e) {
			throw new ResponseStatusException(HttpStatus.OK, e.getMessage());
		}
	}

	@GetMapping("/unique/{facultadId}/{provinciaId}/{localidadId}")
	public ResponseEntity<Localidad> findByUnique(@PathVariable Integer facultadId, @PathVariable Integer provinciaId,
			@PathVariable Integer localidadId) {
		try {
			return ResponseEntity.ok(service.findByUnique(facultadId, provinciaId, localidadId));
		} catch (LocalidadException e) {
			throw new ResponseStatusException(HttpStatus.OK, e.getMessage());
		}
	}

	@GetMapping("/last/{facultadId}/{provinciaId}")
	public ResponseEntity<Localidad> findLast(@PathVariable Integer facultadId, @PathVariable Integer provinciaId) {
		try {
			return ResponseEntity.ok(service.findLast(facultadId, provinciaId));
		} catch (LocalidadException e) {
			throw new ResponseStatusException(HttpStatus.OK, e.getMessage());
		}
	}
}
