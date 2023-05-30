/**
 * 
 */
package um.tesoreria.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.rest.exception.LocalidadException;
import um.tesoreria.rest.model.Localidad;
import um.tesoreria.rest.service.LocalidadService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/localidad")
public class LocalidadController {

	@Autowired
	private LocalidadService service;

	@GetMapping("/provincia/{facultadId}/{provinciaId}")
	public ResponseEntity<List<Localidad>> findAllByProvinciaId(@PathVariable Integer facultadId,
			@PathVariable Integer provinciaId) {
		return new ResponseEntity<List<Localidad>>(service.findAllByProvinciaId(facultadId, provinciaId),
				HttpStatus.OK);
	}

	@PostMapping("/nombre")
	public ResponseEntity<Localidad> findByNombre(@RequestBody Localidad localidad) {
		try {
			return new ResponseEntity<Localidad>(
					service.findByNombre(localidad.getFacultadId(), localidad.getProvinciaId(), localidad.getNombre()),
					HttpStatus.OK);
		} catch (LocalidadException e) {
			throw new ResponseStatusException(HttpStatus.OK, e.getMessage());
		}
	}

	@GetMapping("/unique/{facultadId}/{provinciaId}/{localidadId}")
	public ResponseEntity<Localidad> findByUnique(@PathVariable Integer facultadId, @PathVariable Integer provinciaId,
			@PathVariable Integer localidadId) {
		try {
			return new ResponseEntity<Localidad>(service.findByUnique(facultadId, provinciaId, localidadId),
					HttpStatus.OK);
		} catch (LocalidadException e) {
			throw new ResponseStatusException(HttpStatus.OK, e.getMessage());
		}
	}

	@GetMapping("/last/{facultadId}/{provinciaId}")
	public ResponseEntity<Localidad> findLast(@PathVariable Integer facultadId, @PathVariable Integer provinciaId) {
		try {
			return new ResponseEntity<Localidad>(service.findLast(facultadId, provinciaId), HttpStatus.OK);
		} catch (LocalidadException e) {
			throw new ResponseStatusException(HttpStatus.OK, e.getMessage());
		}
	}
}
